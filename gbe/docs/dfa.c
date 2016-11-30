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
        Ex Ex | Ex "|" Ex | Ex "^" Ex | Ex "-" Ex | Ex "&" Ex.
   with the usual precedence rules.
 */

/* THE SCANNER */
typedef enum {
   EndT, EqualT, CommaT, RParT, RBrT, BarT, DiffT, AndT, ProdT,
   PlusT, StarT, QuestT, ZeroT, OneT, SymT, LParT, LBrT
} Lexical;

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

typedef unsigned char byte;
typedef struct Symbol *Symbol;
typedef struct Exp *Exp;

struct Symbol { char *Name; unsigned Hash; Exp Value; Symbol Next; };

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
static Symbol HashTab[HASH_MAX];

char *CopyS(char *S) {
   char *NewS = (char *)Allocate(strlen(S) + 1);
   strcpy(NewS, S); return NewS;
}

Symbol LastID;
Lexical Scan(void) {
   static char ChBuf[0x100];
   char Ch, *CP; int H;
   do Ch = GET(); while (isspace(Ch));
   switch (Ch) {
      case EOF: return EndT;
      case '|': return BarT;
      case '-': return DiffT;
      case '&': return AndT;
      case '^': return ProdT;
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
      for (CP = ChBuf; isalnum(Ch) || Ch == '_' || Ch == '$'; CP++) {
         if (CP - ChBuf == sizeof ChBuf)
            printf("Out of character space.\n"), exit(1);
         *CP = Ch, Ch = GET();
      }
      if (Ch != EOF) UNGET(Ch);
      goto RetSym;
   } else if (Ch == '"') {
      Ch = GET();
      for (CP = ChBuf; Ch != '"' && Ch != EOF; CP++) {
         if (CP - ChBuf == sizeof ChBuf)
            printf("Out of character space.\n"), exit(1);
         *CP = Ch, Ch = GET();
      }
      if (Ch == EOF) printf("Missing closing \".\n"), exit(1);
      goto RetSym;
   } else {
      ERROR("extra character %c", Ch); return EndT;
   }
RetSym:
   if (CP - ChBuf == sizeof ChBuf) printf("Out of character space.\n"), exit(1);
   *CP = '\0';
   for (H = 0, CP = ChBuf; *CP != '\0'; CP++) H = (H << 1) ^ *CP;
   H &= 0xff;
   for (LastID = HashTab[H]; LastID != 0; LastID = LastID->Next)
      if (strcmp(LastID->Name, ChBuf) == 0) return SymT;
   LastID = (Symbol)Allocate(sizeof *LastID);
   LastID->Name = CopyS(ChBuf); LastID->Value = 0;
   LastID->Hash = H, LastID->Next = HashTab[H], HashTab[H] = LastID;
   return SymT;
}

/* DATA STRUCTURES */
typedef enum {
   ZeroX, OneX, SymX, OptX, StarX, PlusX, CatX, OrX, AndX, DiffX, ProdX
} ExpTag;
typedef struct Term { Symbol X; Exp Q; } *Term;
struct Exp {
   ExpTag Tag; int Hash, State; Exp Tail;
   unsigned long ID;
   byte Stack:1, Normal:1, Unit:1;
   int Terms;
   Term Sum;
   union
   {
      Symbol Leaf; Exp Arg[2];
   } Body;
};

static Exp ExpHash[0x100];

Exp MakeExp(ExpTag Tag, ...) {
   static unsigned long LAB = 0;
   static Exp Zero = 0, One = 0;
   va_list AP; Symbol Sym; char *S;
   int H, Div; Exp HP, E, E0, E1;
   va_start(AP, Tag);
   switch (Tag) {
      case ZeroX:
         if (Zero == 0) {
            Zero = (Exp)Allocate(sizeof *Zero); Zero->ID = LAB++;
            Zero->Tag = ZeroX; Zero->Hash = 0;
            Zero->State = 0, Zero->Stack = 0;
            Zero->Unit = 0, Zero->Terms = 0, Zero->Sum = 0, Zero->Normal = 1;
         }
      return Zero;
      case OneX:
         if (One == 0) {
            One = (Exp)Allocate(sizeof *One); One->ID = LAB++;
            One->Tag = OneX; One->Hash = 1;
            One->State = 0, One->Stack = 0;
            One->Unit = 1, One->Terms = 0, One->Sum = 0, One->Normal = 1;
         }
      return One;
      case SymX:
         Sym = va_arg(AP, Symbol);
         if (Sym->Value == 0) {
            E = (Exp)Allocate(sizeof *E); E->ID = LAB++;
            E->Tag = SymX, E->Hash = Sym->Hash;
            E->State = 0, E->Stack = 0;
            E->Unit = 0, E->Normal = 0;
            E->Body.Leaf = Sym; Sym->Value = E;
         }
      return Sym->Value;
      case PlusX: H = 0x00, Div = 8; goto MakeUnary;
      case StarX: H = 0x20, Div = 8; goto MakeUnary;
      case OptX:  H = 0x40, Div = 0x10; goto MakeUnary;
      MakeUnary:
         E0 = va_arg(AP, Exp);
         H += E0->Hash/Div;
         for (HP = ExpHash[H]; HP != 0; HP = HP->Tail)
            if (E0 == HP->Body.Arg[0]) return HP;
      break;
      case ProdX: H = 0x50, Div = 0x10; goto MakeBinary;
      case AndX:  H = 0x60, Div = 0x10; goto MakeBinary;
      case DiffX: H = 0x70, Div = 0x10; goto MakeBinary;
      case OrX:   H = 0x80, Div = 4; goto MakeBinary;
      case CatX:  H = 0xc0, Div = 4; goto MakeBinary;
      MakeBinary:
         E0 = va_arg(AP, Exp), E1 = va_arg(AP, Exp);
         H += (E0->Hash^E1->Hash)/Div;
         for (HP = ExpHash[H]; HP != 0; HP = HP->Tail)
            if (E0 == HP->Body.Arg[0] && E1 == HP->Body.Arg[1])
               return HP;
      break;
   }
   va_end(AP);
   E = (Exp)Allocate(sizeof *E); E->ID = LAB++;
   E->Tag = Tag;
   E->State = 0, E->Stack = 0, E->Normal = 0;
   E->Body.Arg[0] = E0;
   E->Hash = H, E->Tail = ExpHash[H], ExpHash[H] = E;
   switch (Tag) {
      case PlusX: E->Unit = E0->Unit; break;
      case StarX: case OptX: E->Unit = 1; break;
      case ProdX: case AndX: case CatX:
         E->Body.Arg[1] = E1; E->Unit = E0->Unit && E1->Unit;
      break;
      case DiffX:
         E->Body.Arg[1] = E1; E->Unit = E0->Unit && !E1->Unit;
      break;
      case OrX:
         E->Body.Arg[1] = E1; E->Unit = E0->Unit || E1->Unit;
      break;
   }
   return E;
}

static Exp *AStack = 0, *BStack = 0;
static int AMax = 0, BMax = 0, As, Bs;
void InsertA(Exp E) {
   if (As >= AMax)
      AMax += 4, AStack = Reallocate(AStack, sizeof *AStack * AMax);
   AStack[As++] = E;
}
void InsertB(Exp E) {
   if (Bs >= BMax)
      BMax += 4, BStack = Reallocate(BStack, sizeof *BStack * BMax);
   BStack[Bs++] = E;
}
Exp CatExp(Exp A, Exp B) {
   Exp E;
   for (As = 0; A->Tag == CatX; A = A->Body.Arg[1]) InsertA(A->Body.Arg[0]);
   InsertA(A);
   while (As-- > 0) {
      E = AStack[As];
      if (E->Tag == ZeroX || B->Tag == OneX) B = E;
      else if (E->Tag == OneX || B->Tag == ZeroX) ;
      else B = MakeExp(CatX, E, B);
   }
   return B;
}
Exp BinExp(ExpTag Tag, Exp A, Exp B) {
   Exp C, D; int Diff;
   for (As = 0; A->Tag == Tag; A = A->Body.Arg[1]) InsertA(A->Body.Arg[0]);
   for (Bs = 0; B->Tag == Tag; B = B->Body.Arg[1]) InsertB(B->Body.Arg[0]);
   if (A->ID > B->ID) C = A, InsertB(B); else C = B, InsertA(A);
   As--, Bs--;
   while (As >= 0 || Bs >= 0) {
      Diff = 0;
      if (As >= 0) A = AStack[As]; else Diff = -1;
      if (Bs >= 0) B = BStack[Bs]; else Diff = +1;
      if (Diff == 0) Diff = A->ID - B->ID;
      if (Diff <= 0) Bs--, D = B; else As--, D = A;
      if (Diff == 0 && Tag != ProdX) As--;
      switch (Tag) {
         case OrX: /* 0 | x = x | 0 = x | x = x */
            if (C->Tag == ZeroX) { C = D; continue; }
            if (D->Tag == ZeroX || D == C) continue;
         break;
         case AndX: /* 0 & x = x & 0 = 0, x & x = x */
            if (D->Tag == ZeroX) { C = D; continue; }
            if (C->Tag == ZeroX || D == C) continue;
         break;
         case ProdX: /* 1^x = x = x^1, x^0 = 0 = 0^x */
            if (D->Tag == ZeroX || C->Tag == OneX) { C = D; continue; }
            if (D->Tag == OneX || C->Tag == ZeroX) continue;
         break;
      }
      C = MakeExp(Tag, D, C);
   }
   return C;
}

typedef enum { RULE, EQU, PAR, OPT, OR, DIFF, PROD, AND, CAT } StackTag;
char *Action[9] = {
/*  $=,)]|-&^+*?01x([  */
   "$FABC|-&^+*?.....", /* RULE:       -> Exp $            */
   "GF,BC|-&^+*?.....", /* EQU:   Exp  -> Exp "=" Exp $    */
   "DDA)D|-&^+*?.....", /* PAR:   Exp  -> '(' Exp $ ')'    */
   "EEAE]|-&^+*?.....", /* OPT:   Exp  -> '[' Exp $ ']'    */
   "vvvvvvv&^+*?.....", /* OR:    Exp  -> Exp '+' Exp $    */
   "~~~~~~~&^+*?.....", /* DIFF:  Exp  -> Exp '-' Exp $    */
   "XXXXXXXXX+*?.....", /* PROD:  Exp  -> Exp '*' Exp $    */
   "%%%%%%%%%+*?.....", /* AND:   Exp  -> Exp '&' Exp $    */
   "xxxxxxxxx+*?....."  /* CAT:   Exp  -> Exp Exp $        */
};

typedef struct { StackTag Tag; Exp E; } StackCard;
#define STACK_MAX 100
StackCard Stack[STACK_MAX], *SP;
void PUSH(StackTag Tag, Exp E) {
   if (SP >= Stack + STACK_MAX)
      ERROR("Expression too complex ... aborting."), exit(1);
   SP->Tag = Tag, SP->E = E; SP++;
}
#define TOP ((SP - 1)->Tag)
#define POP() ((--SP)->E)

Exp Parse(void) {
   Lexical L; Symbol ID; Exp E, E0, E1;
   SP = Stack; L = Scan();
LHS:
   if (L == SymT) {
      ID = LastID; L = Scan();
      if (L == EqualT) PUSH(EQU, 0), L = Scan();
      else {
         PUSH(RULE, 0);
         E = MakeExp(SymX, ID); goto END;
      }
   } else PUSH(RULE, 0);
EXP:
   switch (L) {
      case LParT: PUSH(PAR, 0); L = Scan(); goto EXP;
      case LBrT:  PUSH(OPT, 0); L = Scan(); goto EXP;
      case ZeroT: E = MakeExp(ZeroX); L = Scan(); goto END;
      case OneT:  E = MakeExp(OneX); L = Scan(); goto END;
      case SymT:  E = MakeExp(SymX, LastID); L = Scan(); goto END;
      default:    ERROR("Corrupt expression."); return 0;
   }
END:
   switch (Action[TOP][L]) {
      case 'A': ERROR("Extra ','");    L = Scan(); goto END;
      case 'B': ERROR("Unmatched )."); L = Scan(); goto END;
      case 'C': ERROR("Unmatched ]."); L = Scan(); goto END;
      case 'D': ERROR("Unmatched (."); POP(); goto END;
      case 'E': ERROR("Unmatched [."); POP(); goto MakeOpt;
      case 'F': ERROR("Left-hand side of '=' must be symbol."); exit(1);
      case 'G': ERROR("Missing ','."); ID->Value = E; POP(); goto LHS;
      case '$': POP(); return E;
      case ',': ID->Value = E; POP(); L = Scan(); goto LHS;
      case ')': POP(); L = Scan(); goto END;
      case ']': POP();
      case '?': L = Scan();
      MakeOpt: E = MakeExp(OptX, E); goto END;
      case 'x': E = CatExp(POP(), E); goto END;
      case 'v': E = BinExp(OrX, POP(), E); goto END;
      case 'X': E = BinExp(ProdX, POP(), E); goto END;
      case '~': E = MakeExp(DiffX, POP(), E); goto END;
      case '%': E = BinExp(AndX, POP(), E); goto END;
      case '.': PUSH(CAT, E); goto EXP;
      case '|': PUSH(OR, E); L = Scan(); goto EXP;
      case '-': PUSH(DIFF, E); L = Scan(); goto EXP;
      case '&': PUSH(AND, E); L = Scan(); goto EXP;
      case '^': PUSH(PROD, E); L = Scan(); goto EXP;
      case '*': L = Scan(); E = MakeExp(StarX, E); goto END;
      case '+': L = Scan(); E = MakeExp(PlusX, E); goto END;
   }
}

Exp *XStack = 0; int Xs = 0, XMax = 0;
#define X_EXTEND 4
void PushQ(Exp Q) {
   if (Q->Stack) return;
   if (Xs >= XMax)
      XMax += X_EXTEND, XStack = Reallocate(XStack, sizeof *XStack * XMax);
   XStack[Xs++] = Q; Q->Stack = 1;
}

void PopQ(void) { XStack[--Xs]->Stack = 0; }

Exp *STab; int Ss;
void AddState(Exp Q) {
   if (Q->State > 0) return;
   if ((Ss&7) == 0)
      STab = Reallocate(STab, sizeof *STab * (Ss + 8));
   Q->State = Ss; STab[Ss++] = Q;
}

Term TList; int Ts, TMax;
void AddTerm(Symbol X, Exp Q) {
   if (Ts >= TMax)
      TMax += 4, TList = Reallocate(TList, sizeof *TList * TMax);
   TList[Ts].X = X, TList[Ts].Q = Q, Ts++;
}

void FormStates(Exp E) {
   int S, I, Diff; Exp A, B; int AX, BX;
   STab = 0, Ss = 0; AddState(MakeExp(ZeroX)); AddState(E);
   TList = 0, TMax = 0;
   for (S = 0; S < Ss; S++) {
      PushQ(STab[S]);
      while (Xs > 0) {
         E = XStack[Xs - 1];
         if (E->Normal) { PopQ(); continue; }
         switch (E->Tag) {
            case ZeroX: case OneX:
               E->Terms = 0, E->Sum = 0, E->Normal = 1; PopQ();
            break;
            case SymX:
               E->Terms = 1, E->Sum = Allocate(sizeof *E->Sum);
               E->Sum[0].X = E->Body.Leaf, E->Sum[0].Q = MakeExp(OneX);
               E->Normal = 1; PopQ();
            break;
            case OptX:
               A = E->Body.Arg[0];
               if (!A->Normal) { PushQ(A); continue; }
               E->Terms = A->Terms, E->Sum = A->Sum;
               E->Normal = 1; PopQ();
            break;
            case StarX: case PlusX:
               A = E->Body.Arg[0];
               if (!A->Normal) { PushQ(A); continue; }
               B = E->Unit? E: MakeExp(StarX, A);
            Catenate:
               E->Terms = A->Terms;
               E->Sum = Allocate(E->Terms * sizeof *E->Sum);
               for (I = 0; I < E->Terms; I++)
                  E->Sum[I].X = A->Sum[I].X,
                  E->Sum[I].Q = CatExp(A->Sum[I].Q, B);
               E->Normal = 1; PopQ();
            break;
            case CatX: case OrX: case AndX: case DiffX: case ProdX:
               A = E->Body.Arg[0];
               if (!A->Normal) { PushQ(A); continue; }
               B = E->Body.Arg[1];
               if (E->Tag == CatX && !A->Unit) goto Catenate;
               if (!B->Normal) { PushQ(B); continue; }
               for (AX = BX = Ts = 0; AX < A->Terms || BX < B->Terms; ) {
                  Exp dA, dB; Symbol xA, xB;
                  Diff = 0;
                  if (AX >= A->Terms) Diff = 1;
                  else dA = A->Sum[AX].Q, xA = A->Sum[AX].X;
                  if (BX >= B->Terms) Diff = -1;
                  else dB = B->Sum[BX].Q, xB = B->Sum[BX].X;
                  if (Diff == 0) Diff = strcmp(xA->Name, xB->Name);
                  if (Diff <= 0) AX++;
                  if (Diff >= 0) BX++;
                  switch (E->Tag) {
                     case AndX:
                        if (Diff == 0) AddTerm(xA, BinExp(AndX, dA, dB));
                     break;
                     case CatX:
                        if (Diff <= 0) dA = CatExp(dA, B);
                     goto Join;
                     case ProdX:
                        if (Diff <= 0) dA = BinExp(ProdX, dA, B);
                        if (Diff >= 0) dB = BinExp(ProdX, dB, A);
                     case OrX: Join:
                        if (Diff < 0) AddTerm(xA, dA);
                        else if (Diff > 0) AddTerm(xB, dB);
                        else AddTerm(xA, BinExp(OrX, dA, dB));
                     break;
                     case DiffX:
                        if (Diff == 0) dA = MakeExp(DiffX, dA, dB);
                        if (Diff <= 0) AddTerm(xA, dA);
                     break;
                  }
               }
               E->Terms = Ts;
               E->Sum = Allocate(E->Terms * sizeof *E->Sum);
               for (I = 0; I < Ts; I++) E->Sum[I] = TList[I];
               E->Normal = 1; PopQ();
            break;
         }
      }
      E = STab[S];
      for (I = 0; I < E->Terms; I++) AddState(E->Sum[I].Q);
   }
   free(XStack), XMax = 0; free(TList), TMax = 0;
}

static struct Equiv { Exp L, R; } *ETab; int Es, EMax;

void AddEquiv(Exp L, Exp R) {
   Exp Q; int E;
   if (L == R) return;
   if (L->State > R->State) Q = L, L = R, R = Q;
   for (E = 0; E < Es; E++)
      if (L == ETab[E].L && R == ETab[E].R) return;
   if (Es >= EMax)
      EMax += 8, ETab = Reallocate(ETab, sizeof *ETab * EMax);
   ETab[Es].L = L, ETab[Es].R = R, Es++;
}

void MergeStates(void) {
   int Classes, S, S1, E; Exp Q, Q1, L, R;
   ETab = 0, EMax = 0;
   for (S = 1; S < Ss; S++) {
      Q = STab[S];
      if (Q->State < S) continue;
      for (S1 = 0; S1 < S; S1++) {
         int LX, RX;
         Q1 = STab[S1];
         if (Q1->State < S1) continue;
         Es = 0, AddEquiv(Q, Q1);
         for (E = 0; E < Es; E++) {
            L = ETab[E].L, R = ETab[E].R;
            if (L->Unit != R->Unit) goto NOT_EQUAL;
            for (LX = 0, RX = 0; LX < L->Terms && RX < R->Terms; ) {
               int Diff = strcmp(L->Sum[LX].X->Name, R->Sum[RX].X->Name);
               if (Diff < 0) AddEquiv(STab[0], L->Sum[LX++].Q);
               else if (Diff > 0) AddEquiv(STab[0], R->Sum[RX++].Q);
               else AddEquiv(L->Sum[LX++].Q, R->Sum[RX++].Q);
            }
            for (; LX < L->Terms; LX++) AddEquiv(STab[0], L->Sum[LX].Q);
            for (; RX < R->Terms; RX++) AddEquiv(STab[0], R->Sum[RX].Q);
         }
      EQUAL: break;
      NOT_EQUAL: continue;
      }
      if (S1 < S) for (E = 0; E < Es; E++) ETab[E].R->State = ETab[E].L->State;
   }
   for (Classes = 0, S = 0; S < Ss; S++) {
      Q = STab[S];
      Q->State = (Q->State < S)? STab[Q->State]->State: Classes++;
   }
}

void WriteStates(void) {
   int Classes, S, I; Exp Q;
   for (Classes = 1, S = 1; S < Ss; S++) {
      Q = STab[S];
      if (Q->State < Classes) continue;
      Classes++;
      printf("Q%d =", Q->State);
      for (I = 0; I < Q->Terms; I++)
         if (Q->Sum[I].Q->State != 0) break;
      if (Q->Unit) {
         printf(" 1");
         if (I < Q->Terms) printf(" |");
      }
      for (; I < Q->Terms; ) {
         printf(" %s Q%d", Q->Sum[I].X->Name, Q->Sum[I].Q->State);
         for (I++; I < Q->Terms; I++)
            if (Q->Sum[I].Q->State != 0) break;
         if (I < Q->Terms) printf(" |");
      }
      putchar('\n');
   }
   if (Classes < 2) printf("Q0 = 0\n");
}

int main(void) {
   Exp E = Parse();
   if (ERRORS > 0) fprintf(stderr, "%d error(s)\n", ERRORS);
   if (E == 0) exit(1);
   FormStates(E); MergeStates(); WriteStates();
   exit(0);
}
