package com.death.a9gag;

import io.realm.RealmObject;

/**
 * Created by rajora_sd on 1/24/2017.
 */

public class JokesModel extends RealmObject {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJoke() {
        return joke;
    }

    public void setJoke(String joke) {
        this.joke = joke;
    }

    String id;
    String joke;
}
