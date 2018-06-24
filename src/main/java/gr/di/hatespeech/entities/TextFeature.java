package gr.di.hatespeech.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import gr.di.hatespeech.utils.Utils;

@SuppressWarnings("serial")
@Entity
@Table(name = "texts_features")
@NamedQueries({
	@NamedQuery(name=Utils.TEXT_FEATURE_FIND_ALL, query="SELECT t FROM TextFeature t"),
	@NamedQuery(name=Utils.TEXT_FEATURE_FIND_BY_ID, query="SELECT t FROM TextFeature t WHERE t.id = :id"),
	@NamedQuery(name=Utils.TEXT_FEATURE_FIND_BY_TEXT, query="SELECT t FROM TextFeature t WHERE t.text.id = :textId"),
	@NamedQuery(name=Utils.TEXT_FEATURE_FIND_BY_FEATURE, query="SELECT t FROM TextFeature t WHERE t.feature.id = :featureId"),
	@NamedQuery(name=Utils.TEXT_FEATURE_FIND_BY_TEXT_AND_FEATURE, query="SELECT t FROM TextFeature t WHERE t.text.id = :textId AND t.feature.id = :featureId"),
	@NamedQuery(name=Utils.TEXT_FEATURE_FIND_BY_FEATURE_KIND, query="SELECT t FROM TextFeature t WHERE t.feature.kind = :kind")
})
public class TextFeature implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@ManyToOne
	@JoinColumn(name="text_id")
	private Text text;
	@ManyToOne
	@JoinColumn(name="feature_id")
	private Feature feature;
	@Column(name="value")
	private Double value;
	
	public TextFeature() {
		
	}

	public TextFeature(Long id, Text text, Feature feature, Double value) {
		super();
		this.id = id;
		this.text = text;
		this.feature = feature;
		this.value = value;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}

	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
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
		TextFeature other = (TextFeature) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
