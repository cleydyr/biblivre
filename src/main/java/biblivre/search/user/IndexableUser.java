package biblivre.search.user;

import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@AllArgsConstructor
@Document(indexName = "users")
@Setting(settingPath = "/META-INF/elasticsearch/users.index.settings.json")
public class IndexableUser implements Persistable<String> {
    @Id private String id;

    @Field(type = FieldType.Integer)
    private int userId;

    @Field(type = FieldType.Text, analyzer = "name")
    private String name;

    @Field(type = FieldType.Integer)
    private int type;

    private String photoId;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Integer)
    private int loginId;

    @Field(type = FieldType.Date, pattern = "uuuuMMdd HHmmss.SSSXXX")
    private Date created;

    @Field(type = FieldType.Integer)
    private int createdBy;

    @Field(type = FieldType.Date, pattern = "uuuuMMdd HHmmss.SSSXXX")
    private Date modified;

    @Field(type = FieldType.Integer)
    private int modifiedBy;

    @Field(type = FieldType.Boolean)
    private boolean userCardPrinted;

    @Field(type = FieldType.Object)
    private Map<String, String> fields;

    @Field(type = FieldType.Boolean)
    private boolean hasPendingFines;

    @Field(type = FieldType.Boolean)
    private boolean hasPendingLoans;

    @Field(type = FieldType.Boolean)
    private boolean hasLogin;

    @Field(type = FieldType.Boolean)
    private boolean isInactive;

    @Field(type = FieldType.Keyword)
    private String schema;

    @Field(type = FieldType.Keyword)
    private String tenant;

    @Override
    public boolean isNew() {
        return userId > 0;
    }

    @Override
    public String getId() {
        return id;
    }
}
