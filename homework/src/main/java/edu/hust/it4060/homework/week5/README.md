## Telnet Server - Bài 1: 

Kĩ thuật multiprocessing (dựa vào system call `fork` trên Unix) đã được thực hiện thông qua việc multi thread multiplex. 

> [PoolingTCPServer](../../../../../../../../../app/src/main/java/com/kien/network/core/support/server/multiplexing/PollingTCPServer.java) eagerly pool các Selectors (trên Linux, các java Selector sử dụng system call `wepoll`) và assign connection theo round robin. 

Thực tế, Linux native C library implementation `glibc` implement `fork` dựa vào `clone3` system call thay vì `fork`. `glibc` cũng implement `pthread_create` dựa theo `clone3`. Cả 2 đều dùng `clone_internal` để fallback xuống `clone2` khi không thể `clone3`. 

`fork` được implement trên Windows hoàn toàn khác. Linux có thể performance giữa `fork` và `pthread_create` có thể so sánh được nhờ các copy-on-write structure của Linux kernel process. Trên Windows, `fork` sẽ spawn và copy sang process mới. Điều này cũng đúng với BSD (FreeBSD, MacOS). 