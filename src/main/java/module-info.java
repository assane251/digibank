module gm.rahmanproperties.digibank {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires static lombok;
    requires com.jfoenix;
    requires jakarta.validation;
    requires jbcrypt;
    requires fxpopup;
    requires jakarta.persistence;
    requires java.mail;
    requires org.jfree.jfreechart;
    requires jfreechart.fx;
    requires activation;
    requires itextpdf;
    requires org.apache.poi.ooxml;


    opens gm.rahmanproperties.digibank to javafx.fxml;
    exports gm.rahmanproperties.digibank;

    opens gm.rahmanproperties.digibank.controllers to javafx.fxml, fxpopup;
    exports gm.rahmanproperties.digibank.controllers;

    opens gm.rahmanproperties.digibank.controllers.admin to javafx.fxml, fxpopup;
    exports gm.rahmanproperties.digibank.controllers.admin;

    opens gm.rahmanproperties.digibank.controllers.client to javafx.fxml, fxpopup;
    exports gm.rahmanproperties.digibank.controllers.client;

    opens gm.rahmanproperties.digibank.domain;
    exports gm.rahmanproperties.digibank.domain to org.hibernate.orm.core;
    exports gm.rahmanproperties.digibank.service;
    exports gm.rahmanproperties.digibank.utils to jakarta.persistence;
    exports gm.rahmanproperties.digibank.repository;
    exports gm.rahmanproperties.digibank.dtos to org.hibernate.orm.core;
}