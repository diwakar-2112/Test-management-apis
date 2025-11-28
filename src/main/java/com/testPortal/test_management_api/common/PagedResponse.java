package com.testPortal.test_management_api.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

// <T> makes this class Generic. It can hold a list of Projects, TestCases, Users, anything!
@Data
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> content;
    private PageInfo pageInfo;
}