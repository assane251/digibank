module gm.rahmanproperties.optibank {
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


    opens gm.rahmanproperties.optibank to javafx.fxml;
    exports gm.rahmanproperties.optibank;

    opens gm.rahmanproperties.optibank.controllers to javafx.fxml, fxpopup;
    exports gm.rahmanproperties.optibank.controllers;

    opens gm.rahmanproperties.optibank.controllers.admin to javafx.fxml, fxpopup;
    exports gm.rahmanproperties.optibank.controllers.admin;

    opens gm.rahmanproperties.optibank.controllers.client to javafx.fxml, fxpopup;
    exports gm.rahmanproperties.optibank.controllers.client;

    opens gm.rahmanproperties.optibank.domain;
    exports gm.rahmanproperties.optibank.domain to org.hibernate.orm.core;
    exports gm.rahmanproperties.optibank.service;
    exports gm.rahmanproperties.optibank.utils to jakarta.persistence;
    exports gm.rahmanproperties.optibank.repository;
    exports gm.rahmanproperties.optibank.dtos to org.hibernate.orm.core;
}