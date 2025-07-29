package org.refit.spring.receipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.refit.spring.receipt.entity.Receipt;

import java.util.List;

@Data
@AllArgsConstructor
public class RejectedListDto {
    private List<Receipt> list;
}
