package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.SQLException;

/*
    트랜잭션 - 트랜잭션 매니저
*/
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    //    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            // 비즈니스 로직
            bizLogic(fromId, toId, money);
            transactionManager.commit(status); // 성공 시 커밋
        } catch (Exception e) {
            transactionManager.rollback(status);// 실패 시 롤백
            throw new IllegalStateException(e);
        }
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    private void release(Connection con) {
        if (con != null) {
            try {
                /*
                    계속 커넥션을 요청 할 때마다 계속 새로운 커넥션을 연결해주는 방식이 아닌 이 방식은 연결하는데에 너무 많은 시간이 소요되므로
                    실무에서는 연결된 커넥션들이 약 10개 정도가 커넥션 풀에 존재하고 이 커넥션 풀에 있는 커넥션들을 계속 가져와서 디비 작업을
                    사용하게 되는데 이 커넥션 풀에 있던 커넥션의 설정을 con.setAutoCommit(false); 변경한 뒤 바로 con.close()를 하게 되면
                    해당 커넥션은 풀로 돌아갈 때도 해당 설정을 가진 채 돌아가게 되므로 꼭 con.setAutoCommit(true); 이렇게 설정을 해서
                    원래 옵션으로 변경해주고 커넥션 풀에 돌아가도록 해야지만, 다음에 사용할 때 예기치 못한 예외들이 발생할 일이 없어진다.
                */
                con.setAutoCommit(true);
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

}
