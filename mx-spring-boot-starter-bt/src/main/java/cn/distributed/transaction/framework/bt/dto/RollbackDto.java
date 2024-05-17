package cn.distributed.transaction.framework.bt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RollbackDto {
    private String xid;

    private String bid;

    private List<List<String>> locks;

}
