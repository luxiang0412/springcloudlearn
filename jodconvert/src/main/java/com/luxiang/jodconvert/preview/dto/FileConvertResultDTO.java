package com.luxiang.jodconvert.preview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileConvertResultDTO {

  private String status;

  private String targetFileName;

}
