package bootcamp.springSprint.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @AllArgsConstructor
public class ProductPostDTO {
    private int userId;
    private int postId;
    private LocalDate date;
    private ProductDTO detail;
    private int category;
    private double price;
}
