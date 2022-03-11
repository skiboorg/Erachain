package org.erachain.dapp.epoch.memoCards;

import com.google.common.primitives.Ints;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Random;

public class MemoCards_01DAPPTest extends TestCase {

    @Test
    void tt() {

        int ii = 100;
        Random rand = new Random();
        while (ii-- > 0) {
            int rareVal = rand.nextInt(Short.MAX_VALUE * 2);
            int rareRes = (int) ((long) rareVal * 10000L / 1751L / (long) (Short.MAX_VALUE * 2));
            System.out.println("rareVal: " + rareVal + " rareRes: " + rareRes);
        }

    }
}