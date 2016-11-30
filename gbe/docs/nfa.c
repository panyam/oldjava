#include <stdio.h>
#include <ctype.h>
#include <string.h>
#include <stdarg.h>
#include <stdlib.h>

/* Some compilers (gcc on the SUN) may require this: */
#define realloc(X, N) ((X) == 0? malloc(N): realloc((X), (N)))

/* Derived from the following syntax using Quantum Parsing:
   Rule = (ID "=" Ex ",")* Ex.
   Ex = "0" | "1" | ID | "(" Ex ")" | "[" Ex "]" | Ex "+" | Ex "*" | Ex "?" |
        Ex Ex | Ex "|" Ex.
   with the usual precedence rules.
 */

/* THE SCANNER */
typedef enum {
   EndT, CommaT, RParT, RBrT, EqualT, BarT,
   ZeroT, OneT, IdenT, LParT, LBrT, PlusT, StarT, QuestT
} Lexical;

char *LastW;

#define MAX_CHAR 0x1000
static char ChArr[MAX_CHAR], *ChP = ChArr;

static int LINE = 1, ERRORS = 0;
#define MAX_ERRORS 25

static int GET(void) {
   int Ch;
   Ch = getchar();
   if (Ch == '\n') LINE++;
   return Ch;
}

static void UNGET(int Ch) {
   if (Ch == '\n') --LINE;
   ungetc(Ch, stdin);
}

static void ERROR(char *Format, ...) {
   va_list AP;
   fprintf(stderr, "[%d] ", LINE);
   va_start(AP, Format); vfprintf(stderr, Format, AP); va_end(AP);
   fputc('\n', stderr);
   if (++ERRORS == MAX_ERRORS)
      fprintf(stderr, "Reached the %d error limit.\n", MAX_ERRORS), exit(1);
}

Lexical LEX(void) {
   char Ch;
   do Ch = GET(); while (isspace(Ch));
   switch (Ch) {
      case EOF: return EndT;
      case '|': return BarT;
      case '(': return LParT;
      case ')': return RParT;
      case '[': return LBrT;
      case ']': return RBrT;
      case '0': return ZeroT;
      case '1': return OneT;
      case '*': return StarT;
      case '+': return PlusT;
      case '?': return QuestT;
      case '=': return EqualT;
      case ',': return CommaT;
   }
   if (isalpha(Ch) || Ch == '_' || Ch == '$') {
      for (LastW = ChP; isalnum(Ch) || Ch == '_' || Ch == '$'; ChP++) {
         if (ChP - ChArr == MAX_CHAR)
            printf("Out of character space.\n"), exit(1);
         *ChP = Ch, Ch = GET();
      }
      if (Ch != EOF) UNGET(Ch);
      if (ChP - ChArr == MAX_CHAR) printf("Out of character space.\n"), exit(1);
      *ChP++ = '\0';
      return IdenT;
   } else if (Ch == '"') {
      Ch = GET();
      for (LastW = ChP; Ch != '"' && Ch != EOF; ChP++) {
         if (ChP - ChArr == MAX_CHAR)
            printf("Out of character space.\n"), exit(1);
         *ChP = Ch, Ch = GET();
      }
      if (Ch == EOF) printf("Missing closing \".\n"), exit(1);
      if (ChP - ChArr == MAX_CHAR) printf("Out of character space.\n"), exit(1);
      *ChP++ = '\0';
      return IdenT;
   } else {
      ERROR("extra character %c", Ch); return EndT;
   }
}

/* DATA STRUCTURES */
typedef unsigned char byte;
typedef struct Symbol *Symbol;
typedef struct Exp *Exp;
typedef struct Equation *Equation;

struct Symbol { char *Name; int Hash; Symbol Next, Tail; };

void *Allocate(unsigned Bytes) {
   void *X = malloc(Bytes);
   if (X == 0) printf("Out of memory.\n"), exit(1);
   return X;
}

void *Reallocate(void *X, unsigned Bytes) {
   X = realloc(X, Bytes);
   if (X == 0) printf("Out of memory.\n"), exit(1);
   return X;
}

#define HASH_MAX 0x100
static Symbol HashTab[HASH_MAX], FirstB = 0, LastB;

char *CopyS(char *S) {
   char *NewS = (char *)Allocate(strlen(S) + 1);
   strcpy(NewS, S); return NewS;
}

byte Hash(char *S) {
   int H; char *T;
   for (H = 0, T = S; *T != '\0'; T++) H = (H << 1) ^ *T;
   return H&0xff;
}

Symbol LookUp(char *S) {
   Symbol Sym; byte H;
   for (H = Hash(S), Sym = HashTab[H]; Sym != 0; Sym = Sym->Next)
      if (strcmp(Sym->Name, S) == 0) return Sym;
   Sym = (Symbol)Allocate(sizeof *Sym);
   Sym->Name = CopyS(S);
   Sym->Hash = H, Sym->Next = HashTab[H], HashTab[H] = Sym;
   Sym->Tail = 0;
   if (FirstB == 0) FirstB = Sym; else LastB->Tail = Sym;
   return LastB = Sym;
}

typedef enum { SymX, ZeroX, OneX, CatX, OrX, StarX, PlusX, OptX } ExpTag;
struct Exp {
   ExpTag Tag; int Hash, Class; Exp Tail;
   union {
      Symbol Leaf; int *Arg;
   } Body;
};
#define NN 0x200
static Exp ExpHash[NN];

#define EQU_EXTEND 0x100
struct Equation {
   Exp Value; int Hash, State; unsigned IsState:1, Mark:1, Stack:1;
};
static Equation EquTab = 0;
int Equs = 0, EquMax = 0;

int DUP(int A, int B) {
   long L, S;
   S = A + B;
   if (S < NN) L = S*(S + 1)/2 + A;
   else {
      S = 2*(NN - 1) - S, A = NN - 1 - A;
      L = S*(S + 1)/2 + A,
      L = NN*NN - 1 - L;
   }
   return (int)(L/NN);
}

void Store(Symbol S, int Q) {
   int H = 0x100 + S->Hash; Exp E;
   for (E = ExpHash[H]; E != 0; E = E->Tail)
      if (S == E->Body.Leaf) break;
   if (E == 0) {
      E = (Exp)Allocate(sizeof *E);
      E->Tag = SymX, E->Body.Leaf = S;
      E->Hash = H, E->Tail = ExpHash[H], ExpHash[H] = E;
   }
   E->Class = Q;
}

int MakeExp(int Q, ExpTag Tag, ...) {
   va_list AP; Symbol Sym; char *S; int H; byte Args; Exp HP, E; int Q0, Q1;
   va_start(AP, Tag);
   switch (Tag) {
      case SymX:
         Sym = va_arg(AP, Symbol);
         H = 0x100 + Sym->Hash; Args = 0;
         for (HP = ExpHash[H]; HP != 0; HP = HP->Tail)
            if (Sym == HP->Body.Leaf) {
               if (Q != -1 && Q != HP->Class) EquTab[Q].Value = HP;
               return HP->Class;
            }
      break;
      case ZeroX: H = 0; goto MakeNullary;
      case OneX: H = 1; goto MakeNullary;
      MakeNullary:
         Args = 0; HP = ExpHash[H];
         if (HP != 0) {
            if (Q != -1 && Q != HP->Class) EquTab[Q].Value = HP;
            return HP->Class;
         }
      break;
      case PlusX:
         Q0 = va_arg(AP, int); H = 0x02 + EquTab[Q0].Hash*0x0a/0x200;
      goto MakeUnary;
      case StarX:
         Q0 = va_arg(AP, int); H = 0x0c + EquTab[Q0].Hash*0x14/0x200;
      goto MakeUnary;
      case OptX:
         Q0 = va_arg(AP, int); H = 0x20 + EquTab[Q0].Hash/0x10;
      MakeUnary:
         Args = 1;
         for (HP = ExpHash[H]; HP != 0; HP = HP->Tail)
            if (Q0 == HP->Body.Arg[0]) {
               if (Q != -1 && Q != HP->Class) EquTab[Q].Value = HP;
               return HP->Class;
            }
      break;
      case OrX:
         Q0 = va_arg(AP, int), Q1 = va_arg(AP, int);
         H = 0x40 + DUP(EquTab[Q0].Hash, EquTab[Q1].Hash)/8;
      goto MakeBinary;
      case CatX:
         Q0 = va_arg(AP, int), Q1 = va_arg(AP, int);
         H = 0x80 + DUP(EquTab[Q0].Hash, EquTab[Q1].Hash)/4;
      MakeBinary:
         Args = 2;
         for (HP = ExpHash[H]; HP != 0; HP = HP->Tail)
            if (Q0 == HP->Body.Arg[0] && Q1 == HP->Body.Arg[1]) {
               if (Q != -1 && Q != HP->Class) EquTab[Q].Value = HP;
               return HP->Class;
            }
      break;
   }
   va_end(AP);
   E = (Exp)Allocate(sizeof *E);
   E->Tag = Tag;
   if (Tag == SymX) E->Body.Leaf = Sym;
   else {
      E->Body.Arg = (int *)(Args > 0? Allocate(Args*sizeof(int)): 0);
      if (Args > 0) E->Body.Arg[0] = Q0;
      if (Args > 1) E->Body.Arg[1] = Q1;
   }
   E->Hash = H, E->Tail = ExpHash[H], ExpHash[H] = E;
   if (Q == -1) {
      if (Equs == EquMax) {
         EquMax += EQU_EXTEND;
         EquTab = (Equation)Reallocate(EquTab, sizeof *EquTab * EquMax);
      }
      EquTab[Equs].Hash = H, EquTab[Equs].State = -1,
      EquTab[Equs].IsState = 0, EquTab[Equs].Mark = 0, EquTab[Equs].Stack = 0;
      Q = Equs++;
   }
   EquTab[Q].Value = E; E->Class = Q; return Q;
}

typedef enum { RULE, EQU, PAR, OPT, OR, CAT } StackTag;
char *Action[7] = {
/*  $,)]=|01x([+*?  */
   ".ABCH|&&&&&+*?", /* RULE:       -> Exp $ */
   "I=BCH|&&&&&+*?", /* EQU:   Exp  -> Exp "=" Exp $    */
   "DD)FH|&&&&&+*?", /* PAR:   Exp  -> '(' Exp $ ')'    */
   "EEG]H|&&&&&+*?", /* OPT:   Exp  -> '[' Exp $ ']'    */
   "vvvvv|&&&&&+*?", /* OR:    Exp  -> Exp '|' Exp $    */
   "xxxxxx&&&&&+*?"  /* CAT:   Exp  -> Exp Exp $        */
};

typedef struct { StackTag Tag; int Q; } StackCard;
#define STACK_MAX 100
StackCard Stack[STACK_MAX], *SP;
void PUSH(StackTag Tag, int Q) {
   if (SP >= Stack + STACK_MAX)
      ERROR("Expression too complex ... aborting."), exit(1);
   SP->Tag = Tag, SP->Q = Q; SP++;
}
#define TOP ((SP - 1)->Tag)
#define POP() ((--SP)->Q)

int Parse(void) {
   Lexical L; Symbol ID; int RHS, Q0, Q1; Exp E;
   SP = Stack;
LHS:
   L = LEX();
   if (L == IdenT) {
      ID = LookUp(LastW); L = LEX();
      if (L == EqualT) PUSH(EQU, -1), L = LEX();
      else {
         PUSH(RULE, -1);
         RHS = MakeExp(-1, SymX, ID); goto END;
      }
   } else PUSH(RULE, -1);
EXP:
   switch (L) {
      case LParT: PUSH(PAR, -1); L = LEX(); goto EXP;
      case LBrT: PUSH(OPT, -1); L = LEX(); goto EXP;
      case ZeroT: RHS = MakeExp(-1, ZeroX); L = LEX(); goto END;
      case OneT:  RHS = MakeExp(-1, OneX); L = LEX(); goto END;
      case IdenT: RHS = MakeExp(-1, SymX, LookUp(LastW)); L = LEX(); goto END;
      default: ERROR("Corrupt expression."); return -1;
   }
END:
   switch (Action[TOP][L]) {
      case 'A': ERROR("Extra ','"); exit(1);
      case 'B': ERROR("Unmatched ).");  L = LEX(); goto END;
      case 'C': ERROR("Unmatched ].");  L = LEX(); goto END;
      case 'D': ERROR("Unmatched (."); POP(); goto END;
      case 'E': ERROR("Unmatched [."); POP(); goto MakeOpt;
      case 'F': ERROR("( ... ]."); POP(); L = LEX(); goto END;
      case 'G': ERROR("[ ... )."); POP(); L = LEX(); goto MakeOpt;
      case 'H': ERROR("Left-hand side of '=' must be symbol."); exit(1);
      case 'I': ERROR("Missing evaluation."); exit(1);
      case '.': POP(); return RHS;
      case ')': POP(); L = LEX(); goto END;
      case ']': POP();
      case '?': L = LEX();
      MakeOpt: RHS = MakeExp(-1, OptX, RHS); goto END;
      case '=': Store(ID, RHS); POP(); goto LHS;
      case 'v': RHS = MakeExp(-1, OrX, POP(), RHS); goto END;
      case 'x': RHS = MakeExp(-1, CatX, POP(), RHS); goto END;
      case '*': RHS = MakeExp(-1, StarX, RHS); L = LEX(); goto END;
      case '+': RHS = MakeExp(-1, PlusX, RHS); L = LEX(); goto END;
      case '|': PUSH(OR, RHS); L = LEX(); goto EXP;
      case '&': PUSH(CAT, RHS); goto EXP;
   }
}

int *State = 0; int Ss = 0, SMax = 0;
#define S_EXTEND 4
void AddState(int Q) {
   if (EquTab[Q].IsState) return;
   if (Ss == SMax)
      SMax += S_EXTEND, State = Reallocate(State, sizeof *State * SMax);
   EquTab[Q].IsState = 1, EquTab[Q].State = Ss; State[Ss++] = Q;
}

int *XStack = 0; int Xs = 0, XMax = 0;
#define X_EXTEND 4
void PushQ(int Q) {
   if (EquTab[Q].Stack) return;
   if (Xs == XMax)
      XMax += X_EXTEND, XStack = Reallocate(XStack, sizeof *XStack * XMax);
   XStack[Xs++] = Q; EquTab[Q].Stack = 1;
}

void PopQ(void) {
   int Q = XStack[--Xs]; EquTab[Q].Stack = 0;
}

void FormState(int Q) {
   int S;
   AddState(Q);
   for (S = 0; S < Ss; S++) {
      PushQ(State[S]);
      while (Xs > 0) {
         Equation X; int qX, Q, Q1; Exp E, E1;
         qX = XStack[Xs - 1], X = EquTab + qX, E = X->Value;
         if (X->Mark) { PopQ(); continue; }
         switch (E->Tag) {
            case SymX: AddState(MakeExp(-1, OneX));
            case OneX: case ZeroX: X->Mark = 1; PopQ(); break;
            case OptX:
               MakeExp(qX, OrX, MakeExp(-1, OneX), E->Body.Arg[0]);
            break;
            case PlusX:
               Q1 = E->Body.Arg[0];
               MakeExp(qX, CatX, Q1, MakeExp(-1, StarX, Q1));
            break;
            case StarX:
               Q1 = E->Body.Arg[0];
               MakeExp(qX, OrX, MakeExp(-1, OneX), MakeExp(-1, PlusX, Q1));
            break;
            case OrX:
               X->Mark = 1;
               PushQ(E->Body.Arg[0]), PushQ(E->Body.Arg[1]);
            break;
            case CatX: {
               int A, B, Q2;
               Q1 = E->Body.Arg[0], Q2 = E->Body.Arg[1];
               E1 = EquTab[Q1].Value;
               switch (E1->Tag) {
                  case SymX: AddState(Q2); X->Mark = 1; PopQ(); break;
                  case OneX: EquTab[qX].Value = EquTab[Q2].Value; break;
                  case ZeroX: MakeExp(qX, ZeroX); break;
                  case OptX:
                     A = E1->Body.Arg[0];
                     MakeExp(qX, OrX, Q2, MakeExp(-1, CatX, A, Q2));
                  break;
                  case PlusX:
                     A = E1->Body.Arg[0];
                     MakeExp(qX, CatX, A, MakeExp(-1, OrX, Q2, qX));
                  break;
                  case StarX:
                     A = E1->Body.Arg[0];
                     MakeExp(qX, OrX, Q2, MakeExp(-1, CatX, A, qX));
                  break;
                  case OrX:
                     A = E1->Body.Arg[0], B = E1->Body.Arg[1];
                     MakeExp(qX, OrX,
                        MakeExp(-1, CatX, A, Q2), MakeExp(-1, CatX, B, Q2)
                     );
                  break;
                  case CatX:
                     A = E1->Body.Arg[0], B = E1->Body.Arg[1];
                     MakeExp(qX, CatX, A, MakeExp(-1, CatX, B, Q2));
                  break;
               }
            }
            break;
         }
      }
   }
}

void WriteStates(void) {
   int S, X, Q;
   for (S = 0; S < Ss; S++) {
      printf("Q%d =", S);
      PushQ(State[S]);
      for (X = 0; X < Xs; X++) {
         Exp E = EquTab[XStack[X]].Value;
         switch (E->Tag) {
            case ZeroX: break;
            case OneX:  printf(" 1"); break;
            case SymX:
               Q = MakeExp(-1, OneX);
               printf(" %s Q%d", E->Body.Leaf->Name, EquTab[Q].State);
            break;
            case OrX: PushQ(E->Body.Arg[0]), PushQ(E->Body.Arg[1]); break;
            case CatX: {
               int Q1 = E->Body.Arg[0], Q2 = E->Body.Arg[1];
               Exp E1 = EquTab[Q1].Value;
               printf(" %s Q%d", E1->Body.Leaf->Name, EquTab[Q2].State);
            }
            break;
         }
         if (E->Tag != OrX && E->Tag != ZeroX && X < Xs - 1) printf(" |");
      }
      while (Xs > 0) PopQ();
      putchar('\n');
   }
}

void main(void) {
   int Q = Parse();
   if (ERRORS > 0) fprintf(stderr, "%d error(s)\n", ERRORS);
   if (Q == -1) exit(1);
   FormState(Q); WriteStates();
   exit(0);
}
