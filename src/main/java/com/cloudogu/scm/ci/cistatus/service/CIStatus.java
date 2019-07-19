package com.cloudogu.scm.ci.cistatus.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@XmlRootElement(name = "cistatus")
@XmlAccessorType(XmlAccessType.FIELD)
public class CIStatus {
    private String type;
    private String name;
    private Status status;
    private String url;
}
