package org.zerock.projects.dto.search;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int page = 1;
    @Builder.Default
    private int size = 10;
    private String link;
    private String type;
    private String keyword;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate regDate;

    public String[] getType(){
        if (type == null || type.isEmpty()){
            return null;
        }
        return type.split(",");
    }

    public Pageable getPageable(String ... props){
        return PageRequest.of(this.page - 1 , this.size, Sort.by(props).ascending());

    }
    public String getLink(){
        StringBuilder builder = new StringBuilder();
        builder.append("?page=").append(this.page);
        builder.append("&size=").append(this.size);

        if(type != null && !type.isEmpty()) {
            builder.append("&type=").append(type);
        }
        if(keyword != null && !keyword.isEmpty()) {
            try {
                builder.append("&keyword=").append(URLEncoder.encode(keyword,"UTF-8"));  // 한글처리를 위한 코드
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if(regDate != null) {
            builder.append("&regDate=").append(regDate);
        }

        link = builder.toString();

        return link;
    }
}
