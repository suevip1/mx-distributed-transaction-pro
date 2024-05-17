package cn.distributed.transaction.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RollbackDto {
    private String xid;

    private String bid;

    private List<List<String>> locks;

    private List<String> types;
}
