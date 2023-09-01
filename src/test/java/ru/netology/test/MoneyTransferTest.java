package ru.netology.test;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static ru.netology.data.DataHelper.*;


@SuppressWarnings("ALL")
public class MoneyTransferTest {
    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @Test
    void shouldTransferFromCardToCardPositiveTest() {
        open("http://localhost:9999/");
        val loginPage= new LoginPage();
        val authInfo = getAuthInfo();
        val verificationPage = LoginPage.validLogin(authInfo);
        val verificationCode = getVerificationCodeFor(authInfo);
        val dashboardPage = verificationPage.validVerify(verificationCode);
        val firstCardBalance = dashboardPage.getCardBalance(getFirstCardNumber().getCardNumber());
        val secondCardBalance = dashboardPage.getCardBalance(getSecondCardNumber().getCardNumber());
        val transferPage = dashboardPage.depositToFirstCard();
        int amount = 1_000;
        transferPage.transferMoney(amount, getSecondCardNumber());
        val expectedFirstCardBalanceAfter = firstCardBalance + amount;
        val expectedSecondCardBalanceAfter = secondCardBalance - amount;
        Assertions.assertEquals(expectedFirstCardBalanceAfter, dashboardPage.getCardBalance(getFirstCardNumber().getCardNumber()));
        Assertions.assertEquals(expectedSecondCardBalanceAfter, dashboardPage.getCardBalance(getSecondCardNumber().getCardNumber()));

    }

    @Test
    void shouldZeroSumTransferNegativeTest() {
        var loginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        val verificationPage = LoginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        val dashboardPage = verificationPage.validVerify(verificationCode);
        val transferPage = dashboardPage.depositToSecondCard();
        int amount = 0;
        transferPage.transferMoney(amount, DataHelper.getFirstCardNumber());
        transferPage.emptyAmountField();

    }


    @Test
    void shouldTransferFromCardToSameCardNegativeTest() {
        var loginPage = new LoginPage();
        val authInfo = getAuthInfo();
        val verificationPage = LoginPage.validLogin(authInfo);
        val verificationCode = getVerificationCodeFor(authInfo);
        val dashboardPage = verificationPage.validVerify(verificationCode);
        val transferPage = dashboardPage.depositToFirstCard();
        int amount = 1_000;
        transferPage.transferMoney(amount, getFirstCardNumber());
        transferPage.enterAnotherCard();

    }
}
