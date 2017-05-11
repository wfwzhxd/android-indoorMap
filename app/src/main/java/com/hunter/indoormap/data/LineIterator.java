package com.hunter.indoormap.data;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Copy from Apache common-io project. http://commons.apache.org/proper/commons-io/javadocs/api-2.5/index.html
 */
class LineIterator implements Iterator<String>, Closeable {

    // N.B. This class deliberately does not implement Iterable, see https://issues.apache.org/jira/browse/IO-181

    /** The reader that is being read. */
    private final BufferedReader bufferedReader;
    /** The current line. */
    private String cachedLine;
    /** A flag indicating if the iterator has been fully read. */
    private boolean finished = false;

    /**
     * Constructs an iterator of the lines for a <code>Reader</code>.
     *
     * @param reader the <code>Reader</code> to read from, not null
     * @throws IllegalArgumentException if the reader is null
     */
    public LineIterator(final Reader reader) throws IllegalArgumentException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader must not be null");
        }
        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader) reader;
        } else {
            bufferedReader = new BufferedReader(reader);
        }
    }

    public LineIterator(File file) throws FileNotFoundException, UnsupportedEncodingException {
        this(new FileInputStream(file));
    }

    public LineIterator(InputStream inputStream) throws UnsupportedEncodingException {
        this(new InputStreamReader(inputStream, DxfDataSource.DATAFILE_ENCODING));
    }

    //-----------------------------------------------------------------------
    /**
     * Indicates whether the <code>Reader</code> has more lines.
     * If there is an <code>IOException</code> then {@link #close()} will
     * be called on this instance.
     *
     * @return {@code true} if the Reader has more lines
     * @throws IllegalStateException if an IO exception occurs
     */
    public boolean hasNext() {
        if (cachedLine != null) {
            return true;
        } else if (finished) {
            return false;
        } else {
            try {
                while (true) {
                    final String line = bufferedReader.readLine();
                    if (line == null) {
                        finished = true;
                        return false;
                    } else if (isValidLine(line)) {
                        cachedLine = line;
                        return true;
                    }
                }
            } catch(final IOException ioe) {
                close();
                throw new IllegalStateException(ioe);
            }
        }
    }


    protected boolean isValidLine(final String line) {
        return true;
    }

    /**
     * Returns the next line in the wrapped <code>Reader</code>.
     *
     * @return the next line from the input
     * @throws NoSuchElementException if there is no line to return
     */
    public String next() {
        return nextLine();
    }

    /**
     * Returns the next line in the wrapped <code>Reader</code>.
     *
     * @return the next line from the input
     * @throws NoSuchElementException if there is no line to return
     */
    public String nextLine() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more lines");
        }
        final String currentLine = cachedLine;
        cachedLine = null;
        return currentLine;
    }

    public String peepNextLine() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more lines");
        }
        return new String(cachedLine);
    }

    /**
     * Closes the underlying <code>Reader</code> quietly.
     * This method is useful if you only want to process the first few
     * lines of a larger file. If you do not close the iterator
     * then the <code>Reader</code> remains open.
     * This method can safely be called multiple times.
     */
    public void close() {
        finished = true;
        if(bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cachedLine = null;
    }

    /**
     * Unsupported.
     *
     * @throws UnsupportedOperationException always
     */
    public void remove() {
        throw new UnsupportedOperationException("Remove unsupported on LineIterator");
    }

}
