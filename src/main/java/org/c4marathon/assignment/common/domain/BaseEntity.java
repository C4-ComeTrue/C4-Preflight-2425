package org.c4marathon.assignment.common.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@Getter
public abstract class BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@CreatedDate
	@Column(name = "created_date", columnDefinition = "datetime", nullable = false)
	private LocalDateTime createdDate;

	@LastModifiedDate
	@Column(name = "modified_date", columnDefinition = "datetime")
	private LocalDateTime modifiedDate;

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()){
			return false;
		}
		BaseEntity other = (BaseEntity) o;
		return Objects.equals(this.id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
