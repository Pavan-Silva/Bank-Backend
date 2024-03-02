package com.example.accountservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "acc_holders")
public class AccHolder {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 45)
    @Column(name = "name", length = 45)
    private String name;

    @Size(max = 12)
    @Column(name = "nic", length = 12)
    private String nic;

    @Size(max = 10)
    @Column(name = "mobile", length = 10)
    private String mobile;

    @Size(max = 45)
    @Column(name = "address", length = 45)
    private String address;

    @Size(max = 45)
    @Column(name = "email", length = 45)
    private String email;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "gender_id", nullable = false)
    private Gender gender;
}