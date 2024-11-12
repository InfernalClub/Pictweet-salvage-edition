package cl.ucn.disc.dsm.pictwin;

import cl.ucn.disc.dsm.pictwin.model.Persona;
import cl.ucn.disc.dsm.pictwin.model.PicTwin;
import cl.ucn.disc.dsm.pictwin.services.Controller;

import cl.ucn.disc.dsm.pictwin.utils.FileUtils;
import io.ebean.DB;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

@Slf4j
public class TheMain {
    public static void main(String[] args) {
        log.debug("Starting Main ..");

        Controller c = new Controller(DB.getDefault());

        if (c.seed()) {
            log.debug("Seeded the database.");
        }

        log.debug("Registering Persona ..");
        Persona p = c.register("kevin.sotelo@alumnos.ucn.cl","S0t3l0");
        log.debug("Persona: {}", p);

        File file = FileUtils.getResourceFile("antofagasta.jpg");
        log.debug("File: {}", file);

        PicTwin pt = c.addPic(p.getUlid(), -23.6509, -70.3975, file);
        log.debug("PicTwin: {}", pt);

        Persona p2 = c.login("durrutia@ucn.cl", "durrutia123");
        log.debug("Persona_ {}", p2);

        List<PicTwin> pts = c.getPicTwins(p.getUlid());
        for (PicTwin ptt: pts) {
            log.debug("PicTwins: {}", pts);
        }

        log.debug("Done.");
    }



}
