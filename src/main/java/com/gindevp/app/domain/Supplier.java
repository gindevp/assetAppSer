package com.gindevp.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * Nhà cung cấp — mã NCCxxxxxx
 */
@Entity
@Table(name = "supplier")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "code", length = 20, nullable = false, unique = true)
    private String code;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Size(max = 50)
    @Column(name = "tax_code", length = 50)
    private String taxCode;

    @Size(max = 30)
    @Column(name = "phone", length = 30)
    private String phone;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 500)
    @Column(name = "address", length = 500)
    private String address;

    @Size(max = 255)
    @Column(name = "contact_person", length = 255)
    private String contactPerson;

    @Column(name = "created_date")
    private Instant createdDate;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @PrePersist
    public void prePersistSupplier() {
        if (createdDate == null) {
            createdDate = Instant.now();
        }
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Supplier id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Supplier code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public Supplier name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaxCode() {
        return this.taxCode;
    }

    public Supplier taxCode(String taxCode) {
        this.setTaxCode(taxCode);
        return this;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getPhone() {
        return this.phone;
    }

    public Supplier phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return this.email;
    }

    public Supplier email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return this.address;
    }

    public Supplier address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPerson() {
        return this.contactPerson;
    }

    public Supplier contactPerson(String contactPerson) {
        this.setContactPerson(contactPerson);
        return this;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Supplier createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Supplier active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Supplier)) {
            return false;
        }
        return getId() != null && getId().equals(((Supplier) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Supplier{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", taxCode='" + getTaxCode() + "'" +
            ", phone='" + getPhone() + "'" +
            ", email='" + getEmail() + "'" +
            ", address='" + getAddress() + "'" +
            ", contactPerson='" + getContactPerson() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
