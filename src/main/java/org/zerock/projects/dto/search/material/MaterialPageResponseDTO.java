package org.zerock.projects.dto.search.material;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.zerock.projects.dto.MaterialDTO;

import java.util.List;


@Getter
@ToString
public class MaterialPageResponseDTO {

    private int page; // 페이지 쪽
    private int size;

    // 페이징 화면에 보여줄 정보들
    private int total; // 할일 전체개수
    private int start; // 페이지 시작번호
    private int end; // 페이지 끝번호
    private boolean prev; // 이전페이지 존재 여부
    private boolean next; // 다음페이지 존재 여부

    // 실제 할일 목록 정보들
    private List<MaterialDTO>  materialDTOList;

    @Builder
    public MaterialPageResponseDTO(
            MaterialPageRequestDTO materialPageRequestDTO,
            List<MaterialDTO> materialDTOList,
            int total
    ) {
        this.page = materialPageRequestDTO.getPage();
        this.size = materialPageRequestDTO.getSize();

        this.materialDTOList= materialDTOList;
        this.total = total;

        this.end =  (int)(Math.ceil(this.page / 10.0 )) *  10;
        this.start = this.end - 9;
        int last =  (int)(Math.ceil((total/(double)size)));
        this.end =  end > last ? last: end;
        this.prev = this.start > 1;
        this.next =  total > this.end * this.size;
    }
}
