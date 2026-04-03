package liuyuyang.net.web.service;

import liuyuyang.net.dto.email.DismissEmailDTO;
import liuyuyang.net.dto.email.WallEmailDTO;

public interface EmailService {
    void sendDismissEmailData(DismissEmailDTO dto);

    void sendWallReplyEmailData(WallEmailDTO dto);
}
