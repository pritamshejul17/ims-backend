package io.bytecode.ims.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto implements Serializable {

    @NotEmpty(message = "name is required")
    private String name;
    @NotBlank(message = "Price is required")
    private Integer price;
    @NotBlank(message = "Quantity is required")
    private Integer quantity;

}
