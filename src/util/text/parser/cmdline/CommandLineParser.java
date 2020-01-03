package util.text.parser.cmdline;

import java.util.ArrayList;
import java.util.Arrays;


public class CommandLineParser {
    public static String[][] parse(String cmdLine, int debugLevel) {
        final int highestDebugLevel=3;
        debugLevel = Math.abs(debugLevel%(highestDebugLevel+1)); /* Levels restricted to 3, 0=debugging info off, lower debug levels = more restricted debugging info */

        if(debugLevel > 0)
            System.out.printf(  "---- DEBUG START ----\n" +
                            "  highestDebugLevel=%d, debugLevel=%d\n" +
                            "  cmd line='%s'\n\n",
                    highestDebugLevel, debugLevel, cmdLine);

        ArrayList<String> lineTokens = new ArrayList<>();
        ArrayList<String[]> lines = new ArrayList<>();

        boolean dqOpened = false;
        int idx=-1, dqIdx=-1, ndqIdx=-1, len=cmdLine.length();

        while( (idx = cmdLine.indexOf('"', idx+1)) != -1 ) {
            if (debugLevel == highestDebugLevel)
                System.out.printf("  Dq found at idx=%d\n", idx);
            if (!dqOpened) { /* Check for opening dq */
                if( (idx==0) || Character.isWhitespace(cmdLine.charAt(idx-1)) ) { /* // validate opening dq: idx:0 || left:ws */
                    dqOpened = true;
                    dqIdx = idx;

                    if(debugLevel >= 2)
                        System.out.printf("  Valid opening dq found at idx=%d\n", idx);
                }
            } else { /* Check for closing dq */
                if( ((idx+1)==len) || (Character.isWhitespace(cmdLine.charAt(idx+1))) || cmdLine.charAt(idx+1)==';' ) { /* validate closing dq: idx+1:len || right:ws || right:';' */
                    splitCmdLine(debugLevel, lineTokens, lines, cmdLine.substring(ndqIdx+1, dqIdx), cmdLine.substring(dqIdx+1, idx));
                    ndqIdx = idx;
                    dqOpened = false;

                    if(debugLevel >= 2)
                        System.out.printf("  Valid closing dq found at idx=%d\n", idx);
                }
            }
        }

        if(ndqIdx != len)
            splitCmdLine(debugLevel, lineTokens, lines, cmdLine.substring(ndqIdx+1, len) );

        if(lineTokens.size() > 0)
            lines.add( lineTokens.toArray(new String[lineTokens.size()]) ); // Replaced, old version: (new String[]{}) );

        if(debugLevel > 0) {
            System.out.println("\n  All args:");
            int i=0, j=0;
            for(String[] lnTokens : lines) {
                System.out.printf("    Line %d: \n", ++i);
                for (String token : lnTokens)
                    System.out.printf("      [%d] '%s'\n", ++j, token);
                j=0;
            }

            System.out.println( "---- DEBUG END ----\n" );
        }

        return lines.toArray(new String[][]{});
    }

    private static void splitCmdLine(
            int debugLevel,
            ArrayList<String> lineTokens,
            ArrayList<String[]> lines,
            String cmdLineSegment,
            String... extra) {
        cmdLineSegment = cmdLineSegment.trim();
        int idx = -1, lastIdx = 0;
        String lineSeg;

        while( (idx=cmdLineSegment.indexOf(';', idx+1)) != -1 ) {
            lineSeg = cmdLineSegment.substring(lastIdx, idx).trim();
            if(lineSeg.length()>0)
                lineTokens.addAll( Arrays.asList( lineSeg.split(" +") ) );
            lines.add( lineTokens.toArray(new String[]{}) );
            lineTokens.clear();
            lastIdx = idx+1;
        }

        if( (idx != (cmdLineSegment.length()-1)) && (lineSeg = cmdLineSegment.substring(lastIdx, cmdLineSegment.length()).trim()).length()>0 )
            lineTokens.addAll( Arrays.asList( lineSeg.split(" +") ) );

        for(String dqArg : extra)
            lineTokens.add(dqArg);

        if( debugLevel > 0 ) {
            System.out.printf("  splitCmdLine:\n    cmdLineSegment='%s', extra='%s'\n    Added tokens:\n",
                    cmdLineSegment,
                    (extra.length>0)?extra[0]:null);
            for (String[] line : lines) {
                System.out.printf("      [ ");
                for (String token : line)
                    System.out.printf("'%s', ", token);
                System.out.println("\b\b ]");
            }
        }
    }
}
