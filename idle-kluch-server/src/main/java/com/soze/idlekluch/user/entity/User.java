package com.soze.idlekluch.user.entity;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(
  name = "users",
  uniqueConstraints = @UniqueConstraint(columnNames = {"username"})
)
public class User {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "user_id"))
  private EntityUUID userId;

  @Column(name = "username")
  @Size(min = 1, max = 50)
  @NotNull
  private String username;

  @Column(name = "password_hash")
  @NotNull
  private String passwordHash;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "deleted")
  private boolean deleted;

  public User() {

  }

  public EntityUUID getUserId() {
    return userId;
  }

  public void setUserId(EntityUUID userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public boolean getDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  @Override
  public String toString() {
    return "User{" +
      "userId=" + userId +
      ", username='" + username + '\'' +
      ", createdAt=" + createdAt +
      '}';
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final User user = (User) o;
    return Objects.equals(getUserId(), user.getUserId());
  }

  @Override
  public int hashCode() {

    return Objects.hash(getUserId());
  }
}
