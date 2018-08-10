package com.naman14.timber.streaming;

/**
 * Created by programmer jalanan on 16/11/17.
 */


import com.google.gson.annotations.SerializedName;

public class SitusList {

        @SerializedName("id")
        int id;
        @SerializedName("list_situs")
        String list_situs;
        @SerializedName("id_user")
        String id_user;
        @SerializedName("nama_foto")
        String foto;

        public SitusList(String id_user) {
            this.id_user = id_user;
        }

        public int getId() {
            return id;
        }
        public String Listsitus() {
            return list_situs;
        }

        public String getFoto(){
            return foto;
        }

}
