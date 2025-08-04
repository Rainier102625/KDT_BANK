package com.example.bankservice1.model;

public class tokenManager {

    // 1. 싱글톤 인스턴스 생성
    private static final tokenManager instance = new tokenManager();

    private String jwtToken;

    // 2. 외부에서 생성자를 호출하지 못하도록 private으로 설정
    private tokenManager() {}

    // 3. 인스턴스를 가져오는 public static 메서드
    public static tokenManager getInstance() {
        return instance;
    }

    // 4. 토큰을 저장하는 메서드
    public void setJwtToken(String token) {
        this.jwtToken = token;
    }

    // 5. 저장된 토큰을 가져오는 메서드
    public String getJwtToken() {
        return this.jwtToken;
    }

    // 6. 로그아웃 시 토큰을 삭제하는 메서드
    public void clearSession() {
        this.jwtToken = null;
    }
}
