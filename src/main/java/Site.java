import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Site {
    private String name;
    private String href;
    private String date;
}
