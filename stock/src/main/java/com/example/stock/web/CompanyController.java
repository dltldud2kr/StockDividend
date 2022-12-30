package com.example.stock.web;

import com.example.stock.model.Company;
import com.example.stock.persist.entity.CompanyEntity;
import com.example.stock.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    //배당금 검색할 때 자동완성이 되도록 해주는 api
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autoComplete(@RequestParam String keyword) {
        var result = this.companyService.getCompanyNamesByKeyword(keyword); //이렇게(LIKE 연산자) 구현하면 따로 trie 에 저장할 필요가 없음 ( 아래는 trie 사용 )
//        var result = this.companyService.autocomplete(keyword);           //trie 를 사용한  위에 것과 동일문

        return ResponseEntity.ok(result);
    }

    //회사리스트 조회
    @GetMapping
    public ResponseEntity<?> searchCompany(final Pageable pageable){        //pageable.txt 확인
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);

        return ResponseEntity.ok(companies);
    }

    //회사 및 배당금 정보 추가
    @PostMapping
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();
        if (ObjectUtils.isEmpty(ticker)){
            throw new RuntimeException("ticker is empty");
        }
        Company company = this.companyService.save(ticker);
        this.companyService.addAutoCompleteKeyword(company.getName());  //자동완성기능을 위해 이미 찾았던 company 를 넣어줌.
                                                                        //현재 자동완성기능에서 trie 가 아닌 LIKE 문을 사용하고 있기는 함
        return ResponseEntity.ok(company);
    }

    //회사 삭제
    @DeleteMapping
    public ResponseEntity<?> deleteCompany(){
        return null;
    }
}
