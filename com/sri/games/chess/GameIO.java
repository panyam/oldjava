

import java.io.*;

/**
 * An interface for reading games.
 */
public interface GameIO
{
    public Game readGame(InputStream iStream) throws Exception;
    public void writeGame(Game game, OutputStream iStream) throws Exception;
}
