package core;//import static def.dom.Document.*;

import def.dom.HTMLElement;

import static def.dom.Globals.document;

final public class Utils {

    private Utils() {
    }

    private static HTMLElement log = document.getElementById("log");

    static void debug(String str) {
        // проверка положения скрола
        if (Math.abs(log.scrollTop + log.clientHeight - log.scrollHeight) < 1) {
            log.innerText += str + '\n';
            log.scrollTop = log.scrollHeight;
        } else {
            log.innerText += str + '\n';
        }
        // const d = log.innerText.distance - 200;
        // if (d > 0) {
        //     log.innerText = log.innerText.substring(d + str.distance, 201 + str.distance) + str + '\n';
        // } else {
        //     log.innerText += str + '\n';
        // }
    }
}
