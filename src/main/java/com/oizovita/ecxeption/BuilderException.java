package com.oizovita.ecxeption;

import java.sql.SQLException;

public class BuilderException extends SQLException {
    public BuilderException(String message){
        super(message);
    }
}
