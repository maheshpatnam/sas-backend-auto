package sas.sdet.techtest.repository;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sas.sdet.techtest.domain.Tournament;
import sas.sdet.techtest.domain.User;

import javax.persistence.EntityManager;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class RepositoryClassTest {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private RepositoryClass  repository;

    private static final String USER_NAME = "userName";
    private static final String TOURNAMENT_NAME = "tournamentName";
    private static final String TOURNAMENT_TYPE = "tournamentType";
    private static final int DEXTERITY = 2;
    private static final int PROFESSIONALISM = 2;

    private User user;
    private Tournament tournament;

    @BeforeEach
    public void init() {
        user = new User();
        user.setDexterity(2);
        user.setName(USER_NAME);

        tournament = new Tournament();
        tournament.setName(TOURNAMENT_NAME);
        tournament.setType(TOURNAMENT_TYPE);
        tournament.setProfessionalism(PROFESSIONALISM);
    }
    @Test
    public void testLoadUser() {


        Mockito.doReturn(user).when(mockEntityManager).find(User.class, USER_NAME);
        var loadedUser = repository.loadUser(USER_NAME);
        assertThat(loadedUser).isNotNull();
        assertThat(loadedUser.getName()).isEqualTo(USER_NAME);
        assertThat(loadedUser.getDexterity()).isEqualTo(DEXTERITY);
    }

    @Test
    public void testLoadItem() {

        Mockito.doReturn(tournament)
                .when(mockEntityManager).find(Tournament.class, TOURNAMENT_NAME);
        var loadedTournament = repository.loadItem(TOURNAMENT_NAME);
        assertThat(loadedTournament).isNotNull();
        assertThat(loadedTournament.getName()).isEqualTo(TOURNAMENT_NAME);
        assertThat(loadedTournament.getProfessionalism()).isEqualTo(PROFESSIONALISM);
        assertThat(loadedTournament.getType()).isEqualTo(TOURNAMENT_TYPE);
    }

    @Test
    public void testSuccessfulOrder() throws NotEnoughProException {
        Mockito.doReturn(user).when(mockEntityManager).find(User.class, USER_NAME);
        Mockito.doReturn(tournament).when(mockEntityManager).find(Tournament.class, TOURNAMENT_NAME);
        var order = repository.order(USER_NAME, TOURNAMENT_NAME);
        assertThat(order).isNotNull();

        var user = order.getUser();
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo(USER_NAME);
        assertThat(user.getDexterity()).isEqualTo(DEXTERITY);
        var tournament = order.getItem();
        assertThat(tournament).isNotNull();
        assertThat(tournament.getName()).isEqualTo(TOURNAMENT_NAME);
        assertThat(tournament.getProfessionalism()).isEqualTo(PROFESSIONALISM);
        assertThat(tournament.getType()).isEqualTo(TOURNAMENT_TYPE);
    }

    @Test
    public void testNotEnoughProException() {
        user.setDexterity(1); // setting users dexterity less than tournaments professionalism
        Mockito.doReturn(user).when(mockEntityManager).find(User.class, USER_NAME);
        Mockito.doReturn(tournament)
                .when(mockEntityManager).find(Tournament.class, TOURNAMENT_NAME);

        Assertions.assertThrows(NotEnoughProException.class,
                () -> repository.order(USER_NAME, TOURNAMENT_NAME)
        , "NotEnoughProException was expected as users dexterity is less than tournaments professionalism");
    }

    @Test
    public void testOrderReturnsNullWhenUserAndTournamentDoesNotExist() throws NotEnoughProException {
        Mockito.doReturn(null).when(mockEntityManager).find(User.class, USER_NAME);
        Mockito.doReturn(null).when(mockEntityManager).find(Tournament.class, TOURNAMENT_NAME);
        var order = repository.order(USER_NAME, TOURNAMENT_NAME);
        assertThat(order).isNull();
    }

}
