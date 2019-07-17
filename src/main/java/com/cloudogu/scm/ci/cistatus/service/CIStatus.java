package com.cloudogu.scm.ci.cistatus.service;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@XmlRootElement(name = "cistatus")
@XmlAccessorType(XmlAccessType.FIELD)
public class CIStatus {
    private String name;
    private String type;
    private Status status;
    private String url;
}
