package run;

import effekt.*;

public class Suspend implements Runnable {

    public void test() throws Effects {
        System.out.println("Hello");
        suspend();
        System.out.println("World");
    }

    public void run() {
        Effekt.run(() -> {
            System.out.println("Ready");
            Effekt.pushPrompt(p, () -> { test(); return null; });
            System.out.println("Steady");
            resume();
            System.out.println("Go!");
            return null;
        });
    }


    private final Prompt p = new Prompt() {};
    private Continuation k;

    public void suspend() throws Effects {
        Effekt.withSubcontinuation(p, k -> { this.k = k; return null; });
    }

    public void resume() throws Effects {
        k.resume(null);
    }
}