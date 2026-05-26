package hello.jdbc.repository;

import hello.jdbc.domain.Member;

import java.sql.SQLException;

/*
    해당 MemberRepositoryEx 인터페이스는 특정 기술 JDBC에 종속된 인터페이스이다.
    구현 기술을 쉽게 변경하기 위해 인터페이스를 도입하더라도 SQLException과 같은 특정 구현 기술에 종속적인 체크 예외를 사용하게 되면
    인터페이스에도 해당 예외를 포함해야 한다. 하지만 이것은 우리가 원하던 순수한 인터페이스가 아니다.
    JDBC 기술에 종속적인 인터페이스일 뿐이다. 인터페이스를 만드는 목적은 구현체를 쉽게 변경하기 위함인데, 이미 인터페이스가 특정 구현 기술에 오염이 되어 버린 것이다.
    향후 JDBC가 아닌 다른 기술로 변경한다면 인터페이스 자체를 변경해야 한다.
*/
public interface MemberRepositoryEx {
    Member save(Member member) throws SQLException;
    Member findById(String memberId) throws SQLException;
    void update(String memberId, int money) throws SQLException;
    void delete(String memberId) throws SQLException;
}
