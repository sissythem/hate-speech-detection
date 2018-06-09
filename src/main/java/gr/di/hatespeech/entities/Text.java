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
 * Represents a comment (tweet, post on facebook etc) containing the message
 * body and the class name (hate speech or not)
 * @author sissy
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "texts")
@NamedQueries({ 
	@NamedQuery(name = Utils.TEXT_FIND_ALL, query = "SELECT t FROM Text t"),
	@NamedQuery(name = Utils.TEXT_FIND_BY_ID, query = "SELECT t FROM Text t WHERE t.id = :id"),
	@NamedQuery(name = Utils.TEXT_FIND_BY_LABEL, query = "SELECT t FROM Text t WHERE t.label = :label"),
	@NamedQuery(name=Utils.TEXT_FIND_BY_OLD_LABEL, query = "SELECT t FROM Text t WHERE t.oldLabel = :oldLabel")
})
public class Text implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@NotNull
	@Size(max = 1000)
	@Column(name = "body")
	private String body;
	@NotNull
	@Size(max = 100)
	@Column(name = "label")
	private String label;
	@NotNull
	@Size(max = 100)
	@Column(name = "old_label")
	private String oldLabel;
	@NotNull
	@Column(name="dataset")
	private Integer dataset;
	@Size(max=100)
	@Column(name="tweet_id")
	private String tweetId;
	@NotNull
	@Size(max = 1000)
	@Column(name="processed_body")
	private String prepMessage;
	@OneToMany(mappedBy = "text")
	private List<TextFeature> features = new ArrayList<>();

	public Text() {

	}

	public Text(String body, String label) {
		super();
		this.body = body;
		this.label = label;
	}

	public Text(Long id, String body, String label) {
		super();
		this.id = id;
		this.body = body;
		this.label = label;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getOldLabel() {
		return oldLabel;
	}

	public void setOldLabel(String oldLabel) {
		this.oldLabel = oldLabel;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getDataset() {
		return dataset;
	}

	public void setDataset(Integer dataset) {
		this.dataset = dataset;
	}

	public String getTweetId() {
		return tweetId;
	}

	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}

	public String getPrepMessage() {
		return prepMessage;
	}

	public void setPrepMessage(String prepMessage) {
		this.prepMessage = prepMessage;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Text other = (Text) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		return true;
	}
	
}
