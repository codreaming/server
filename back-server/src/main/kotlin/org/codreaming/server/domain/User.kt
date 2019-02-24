package org.codreaming.server.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull

@Entity
@Table(name = "users",
        uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("email"))])
class User(
        @Column(nullable = false)
        var name: String,

        @Email
        @Column(nullable = false)
        var email: String,

        var imageUrl: String = "",

        @NotNull
        @Enumerated(EnumType.STRING)
        var provider: AuthProvider,

        var providerId: String = ""
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    var emailVerified: Boolean = false

    @JsonIgnore
    var password: String = ""
}

enum class AuthProvider {
    LOCAL,
    GOOGLE
}

