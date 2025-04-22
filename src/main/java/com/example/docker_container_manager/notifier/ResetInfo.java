package com.example.docker_container_manager.notifier;

import lombok.Data;

@Data
class ResetInfo {
    private int forced = 0;
    private int notForced = 0;
}
