package your.package.here;  // Adjust as needed

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailConstants;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * Demonstrates various JUnit tests for the Apache Commons Email functionality.
 */
public class MailMessageTest {

    /**
     * The email instance that will be tested.
     */
    private Email mail;

    /**
     * Creates a fresh SimpleEmail before each test runs.
     */
    @Before
    public void setup() {
        mail = new SimpleEmail();
    }

    /**
     * Resets the mail instance after each test finishes.
     */
    @After
    public void teardownTest() {
        mail = null;
    }

    // ========== BCC Tests ==========

    @Test
    public void testAddingMultipleBccRecipients() throws Exception {
        // Define an array of valid BCC recipients
        String[] emailAddrs = {"john.doe@company.net", "jane.smith@company.net"};

        // Add them all at once
        mail.addBcc(emailAddrs);

        // Retrieve and validate the stored addresses
        String first = mail.getBccAddresses().get(0).getAddress();
        String second = mail.getBccAddresses().get(1).getAddress();

        // Confirm each address matches what was provided
        Assert.assertEquals(emailAddrs[0], first);
        Assert.assertEquals(emailAddrs[1], second);
    }

    @Test(expected = EmailException.class)
    public void testAddingNullBccArray() throws Exception {
        // Expect an EmailException when passing null
        mail.addBcc(null);
    }

    @Test(expected = EmailException.class)
    public void testAddingEmptyBccArray() throws Exception {
        // Provide an empty array; this should also trigger an exception
        String[] noAddresses = {};
        mail.addBcc(noAddresses);
    }

    // ========== CC Tests ==========

    @Test
    public void testAddingValidCcAddress() throws Exception {
        String addr = "contact@company.net";

        // Add a CC recipient
        mail.addCc(addr);

        // Retrieve the address to ensure it was set properly
        String actual = mail.getCcAddresses().get(0).getAddress();
        Assert.assertEquals(addr, actual);
    }

    @Test(expected = EmailException.class)
    public void testAddingInvalidCcAddress() throws Exception {
        // Supplying a clearly invalid email should raise an EmailException
        String badAddr = "this is not a valid email";
        mail.addCc(badAddr);
    }

    // ========== Header Tests ==========

    @Test
    public void testAddingValidHeader() throws Exception {
        // Test that a valid header can be added without errors
        String name = "Content-Language";
        String value = "en-US";
        mail.addHeader(name, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingHeaderWithNullName() throws Exception {
        // Passing null as a header name should be illegal
        mail.addHeader(null, "some-value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingHeaderWithEmptyValue() throws Exception {
        // Supplying an empty string for the value should be illegal
        mail.addHeader("X-Priority", "");
    }

    // ========== Reply-To Tests ==========

    @Test
    public void testAddingReplyToAddress() throws Exception {
        String addr = "support@company.net";
        String name = "Customer Support";
        mail.addReplyTo(addr, name);

        // Verify the address and the personal name attached
        String actualAddr = mail.getReplyToAddresses().get(0).getAddress();
        String actualName = mail.getReplyToAddresses().get(0).getPersonal();

        Assert.assertEquals(addr, actualAddr);
        Assert.assertEquals(name, actualName);
    }

    @Test(expected = EmailException.class)
    public void testAddingBadReplyToAddress() throws Exception {
        // Invalid address is expected to fail
        String badAddr = "invalid@@malformed";
        String name = "Bad Address";
        mail.addReplyTo(badAddr, name);
    }

    // ========== MIME Message Tests ==========

    @Test
    public void testBuildingCompleteMessage() throws Exception {
        // Configure a fully populated email
        mail.setHostName("smtp.company.net");
        mail.setFrom("system@company.net");
        mail.addTo("recipient@othercompany.net");
        mail.addCc("manager@company.net");
        mail.addBcc("archive@company.net");
        mail.addReplyTo("no-reply@company.net");
        mail.setContent("Here is the system report", EmailConstants.TEXT_PLAIN);
        mail.addHeader("X-System", "AutoReport");

        // Build the MIME message
        mail.buildMimeMessage();

        // Fetch the MIME message and check its content type
        MimeMessage msg = mail.getMimeMessage();
        Assert.assertEquals(EmailConstants.TEXT_PLAIN, msg.getContentType());
    }

    @Test(expected = IllegalStateException.class)
    public void testBuildingMessageTwice() throws Exception {
        // Build a message once...
        mail.setHostName("smtp.company.net");
        mail.setFrom("system@company.net");
        mail.addTo("recipient@othercompany.net");
        mail.setContent("Test message", EmailConstants.TEXT_PLAIN);
        mail.buildMimeMessage();

        // ...and attempt to build it again, which should trigger an exception
        mail.buildMimeMessage();
    }

    @Test(expected = EmailException.class)
    public void testBuildingMessageWithoutSender() throws Exception {
        // Omit the 'from' address, which should cause a failure
        mail.setHostName("smtp.company.net");
        mail.addTo("recipient@othercompany.net");
        mail.buildMimeMessage();
    }

    @Test(expected = EmailException.class)
    public void testBuildingMessageWithoutRecipient() throws Exception {
        // Omit the recipient address, which should also cause a failure
        mail.setHostName("smtp.company.net");
        mail.setFrom("system@company.net");
        mail.buildMimeMessage();
    }

    // ========== Hostname Tests ==========

    @Test
    public void testGettingHostnameViaSession() throws Exception {
        String host = "smtp.company.net";
        mail.setHostName(host);

        // Retrieve hostname from the session object
        Session session = mail.getMailSession();

        // Make sure it matches what was initially set
        Assert.assertEquals(host, session.getProperty(EmailConstants.MAIL_HOST));
        Assert.assertEquals(host, mail.getHostName());
    }

    @Test
    public void testGettingHostnameDirect() throws Exception {
        // Just confirm setHostName() and getHostName() are consistent
        String host = "smtp.company.net";
        mail.setHostName(host);
        Assert.assertEquals(host, mail.getHostName());
    }

    @Test
    public void testSettingEmptyHostname() throws Exception {
        // Providing an empty string should nullify the hostname
        mail.setHostName("");
        Assert.assertNull(mail.getHostName());
    }

    // ========== Mail Session Tests ==========

    @Test
    public void testCreatingValidMailSession() throws Exception {
        // Ensure a valid hostname produces a proper session
        String host = "smtp.company.net";
        mail.setHostName(host);

        Session session = mail.getMailSession();
        String configuredHost = session.getProperty(EmailConstants.MAIL_HOST);

        Assert.assertNotNull("Session should be created", session);
        Assert.assertEquals("The session host should match the specified host", 
                            host, configuredHost);
    }

    @Test(expected = EmailException.class)
    public void testCreatingSessionWithNoHost() throws Exception {
        // Setting an empty string leads to an exception when retrieving the session
        mail.setHostName("");
        mail.getMailSession();
    }

    // ========== From Address Tests ==========

    @Test
    public void testSettingValidFromAddress() throws Exception {
        // A correct email address should be set without issues
        String addr = "marketing@company.net";
        mail.setFrom(addr);

        // Double-check the configured address
        String configured = mail.getFromAddress().getAddress();
        Assert.assertEquals("The configured From address should match the specified one", 
                            addr, configured);
    }

    @Test(expected = EmailException.class)
    public void testSettingInvalidFromAddress() throws Exception {
        // An obviously invalid email should fail
        String badAddr = "not a real email";
        mail.setFrom(badAddr);
    }

    // ========== Miscellaneous Tests ==========

    @Test
    public void testSettingAndGettingSentDate() throws Exception {
        // Assign a particular date
        Date date = new Date(123456789L);
        mail.setSentDate(date);

        // Verify the stored date is consistent
        Date retrieved = mail.getSentDate();
        Assert.assertEquals("Both timestamps should match", 
                            date.getTime(), retrieved.getTime());
    }

    @Test
    public void testSettingSocketTimeout() throws Exception {
        // Confirm a custom socket timeout is set and retrieved properly
        int timeout = 7500;
        mail.setSocketConnectionTimeout(timeout);
        Assert.assertEquals("Socket timeout should match the given value", 
                            timeout, mail.getSocketConnectionTimeout());
    }
}