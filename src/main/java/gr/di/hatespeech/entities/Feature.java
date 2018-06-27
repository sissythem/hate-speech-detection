package gr.di.hatespeech.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import gr.di.hatespeech.utils.Utils;

/**
 * Represents a Feature(description and kind). Each text needs to have a value for each Feature
 */
@SuppressWarnings("serial")
@Entity
@Table(name= "features")
@NamedQueries({
	@NamedQuery(name=Utils.FEATURE_FIND_ALL, query="SELECT f FROM Feature f"),
	@NamedQuery(name=Utils.FEATURE_FIND_BY_ID, query="SELECT f FROM Feature f WHERE f.id = :id"),
	@NamedQuery(name=Utils.FEATURE_FIND_BY_KIND, query="SELECT f FROM Feature f WHERE f.kind = :kind"),
	@NamedQuery(name=Utils.FEATURE_FIND_BY_DESCRIPTION, query="SELECT f FROM Feature f WHERE f.description = :description")
})
public class Feature implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@NotNull
	@Size(max=500)
	@Column(name="description")
	private String description;
	@Column(name="kind")
	private String kind;
	@OneToMany(mappedBy = "feature")	
	private List<TextFeature> texts = new ArrayList<>();
	
	public Feature() {

	}

	public Feature(Long id, String description) {
		super();
		this.id = id;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feature other = (Feature) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
