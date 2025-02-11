//package com.mcq.repository;
//
//import com.mcq.entity.QuizInvite;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import java.util.Optional;
//
//@Repository
//public interface QuizInviteRepository extends JpaRepository<QuizInvite, Long> {
//
//    // Use this method to check if a quiz invite exists for a given quiz, user ID, and that the invitation status is true.
//    boolean existsByQuizIdAndUser_IdAndInvitedStatus(Long quizId, Long userId, boolean invitedStatus);
//
//    // (Optional) If you also want to check by username, you can have:
//    boolean existsByQuizIdAndUserUsernameAndInvitedStatus(Long quizId, String username, boolean invitedStatus);
//
//    // (Optional) Other methods as needed
//    Optional<QuizInvite> findByQuizIdAndUserId(Long quizId, Long userId);
//    boolean existsByQuizIdAndUserId(Long quizId, Long userId);
//
//    boolean existsByQuizIdAndUserUsernameAndInvitedStatus(Long quizId, String username, boolean invitedStatus);
//
//    // New method to check if the user is invited with a given status
//    boolean existsByQuizIdAndUserIdAndInvitedStatus(Long quizId, Long userId, boolean invitedStatus);
//    boolean existsByQuizIdAndUser_IdAndInvitedStatus(Long quizId, Long userId, boolean invitedStatus);
//
//    Optional<QuizInvite> findByQuizIdAndUserId(Long quizId, Long userId);
//
//
//}

//package com.mcq.repository;
//
//import com.mcq.entity.QuizInvite;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import java.util.Optional;
//
//@Repository
//public interface QuizInviteRepository extends JpaRepository<QuizInvite, Long> {
//
//    // Check if a quiz invite exists for the specified quiz ID, user ID, and with invitedStatus true.
//    boolean existsByQuizIdAndUser_IdAndInvitedStatus(Long quizId, Long userId, boolean invitedStatus);
//
//    // Optional: Check by username if needed.
//    boolean existsByQuizIdAndUserUsernameAndInvitedStatus(Long quizId, String username, boolean invitedStatus);
//
//    Optional<QuizInvite> findByQuizIdAndUserId(Long quizId, Long userId);
//}
//
package com.mcq.repository;

import com.mcq.entity.QuizInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface QuizInviteRepository extends JpaRepository<QuizInvite, Long> {
    boolean existsByQuiz_IdAndUser_IdAndInvitedStatus(Long quizId, Long userId, boolean invitedStatus);

    // This method uses an underscore (_) to navigate into the associated User entity’s id.
 //   boolean existsByQuizIdAndUser_IdAndInvitedStatus(Long quizId, Long userId, boolean invitedStatus);

    // (Optional) If you need a method using username:
    boolean existsByQuizIdAndUserUsernameAndInvitedStatus(Long quizId, String username, boolean invitedStatus);

    // (Optional) Other helper methods
    Optional<QuizInvite> findByQuizIdAndUserId(Long quizId, Long userId);
}

