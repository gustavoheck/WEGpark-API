package com.weg.WEGpark.rh;

import com.weg.WEGpark.rh.shared.filter.FindUserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.CompletableFuture;

public record GetParkUsersEvent(
        CompletableFuture<Page<Record>> eventResponse,

        FindUserFilter findUserFilter,

        Pageable pageable
) {
}
