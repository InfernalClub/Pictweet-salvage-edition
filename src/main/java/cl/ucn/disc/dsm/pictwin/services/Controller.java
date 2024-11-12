package cl.ucn.disc.dsm.pictwin.services;

import cl.ucn.disc.dsm.pictwin.model.Persona;
import cl.ucn.disc.dsm.pictwin.model.Pic;
import cl.ucn.disc.dsm.pictwin.model.PicTwin;

import com.password4j.Password;

import io.ebean.Database;
import io.ebean.annotation.Transactional;


import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;


@Slf4j
public class Controller {

    private final Database database;

    public Controller(@NonNull final Database database) {this.database = database;}

    /** The seed of the database. */
    public Boolean seed() {

        // dinf the Persona size
        int personaSize = new QPersona().findCount();
        log.debug("Personas in database: {}", personaSize);

        //if the Persona exists -> don´t seed!
        if (personaSize != 0) {
            return Boolean.FALSE;
        }
        log.debug("Can´t find data, seeding the database ..");

        // seed the Persona
        Persona persona = this.register("durrutia@ucn.cl","durrutia123");
        log.debug("Persona registered: {}", persona);

        log.debug("Database seeded.");

        return Boolean.TRUE;
    }

    @Transactional
    public Persona register(@NonNull final String email, @NonNull final String password) {

        // hash the password
        String hashedPassword = Password.hash(password).withBcrypt().getResult();
        log.debug("Hashed password: {}", hashedPassword);

        // build the Persona
        Persona persona = Persona.builder().email(email).password(hashedPassword).strikes(0).blocked(false).build();

        // save the Persona
        this.database.save(persona);
        log.debug("Persona saved: {}", persona);


        return persona;
    }

    public Persona login(@NonNull final String email, @NonNull final String password) {

        // find the Persona
        Persona persona = new QPersona().email.equalTo(email).findOne();
        if (persona == null) {
            throw  new RuntimeException("User not found");
        }

        // check the password
        if(!Password.check(password, persona.getPassword()).withBcrypt()) {
            throw new RuntimeException("Wrong password");
        }

        return persona;
    }

    /** Add a new Pic */
    @Transactional
    public PicTwin addPic(@NonNull String ulidPersona, @NonNull Double latitude, @NonNull Double longitude, @NonNull File picture) {

        // read the file
        byte[] data = readAllBytes(picture);

        // find the Persona
        Persona persona = new QPersona().Ulid.equialTo(ulidPersona).findOne();
        log.debug("Persona found: {}", persona);

        // save the pic
        Pic pic = Pic.builder().latitude(latitude).longitude(longitude).reports(0).date(Instant.now()).photo(data).bloqued(false).views(0).persona(persona).build();
        log.debug("Pic to save: {}", pic);
        this.database.save(pic);

        // save the Pictwin
        PicTwin picTwin = PicTwin.builder().expiration(Instant.now().plusSeconds(7*24*60*60)).expired(false).reported(false).persona(persona).pic(pic).twin(pic).build();
        log.debug("PicTwin to save: {}", picTwin);
        this.database.save(picTwin);

        return picTwin;

    }
    /** Read all the bytes from a file*/
    private static byte[] readAllBytes(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Can't read the file", e);
        }
    }

    /** Get the PicTwins. */
    public List<PicTwin> getPicTwins(@NonNull String ulidPersona) {
        return new QPicTwin().persona.ulid.equalTo(ulidPersona).findList();
    }

}
