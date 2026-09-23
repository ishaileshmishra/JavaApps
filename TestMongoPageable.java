import org.springframework.data.domain.*;
public class TestMongoPageable {
    public static void main(String[] args) {
        Pageable p = org.springframework.data.domain.PageRequest.of(0, 20);
        System.out.println("Page size: " + p.getPageSize());
        System.out.println("Offset: " + p.getOffset());
    }
}
