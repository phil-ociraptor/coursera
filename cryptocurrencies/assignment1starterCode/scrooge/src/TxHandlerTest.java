import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;

// Thanks to Konstantin Kanishchev for writing this test harness
// https://www.coursera.org/learn/cryptocurrency/programming/KOo3V/scrooge-coin/discussions/threads/3Gng5LcoEeaYcRJ-aKpq1A?sort=upvotesDesc&page=1
public class TxHandlerTest {
    public static void main(String[] args)
        throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
    {
        // This generates keypairs
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        // This hashes stuff
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        // This creates signatures
        Signature sig = Signature.getInstance("SHA256withRSA");

        // Scroodge generates a key pair
        keyGen.initialize(512);
        KeyPair scroodge  = keyGen.generateKeyPair();

        // Creates genesis transaction
        Transaction genesis = new Transaction();
        genesis.addOutput(100, scroodge.getPublic());

        //Hashes it
        genesis.setHash(md.digest(genesis.getRawTx()));

        // Adds it to the pool
        UTXOPool pool = new UTXOPool();
        UTXO utxo = new UTXO(genesis.getHash(), 0);
        pool.addUTXO(utxo, genesis.getOutput(0));

        // Goofy creates his pair
        keyGen.initialize(512);
        KeyPair goofy    = keyGen.generateKeyPair();

        //Scroodge makes a transaction to Goofy
        Transaction send = new Transaction();
        send.addInput(genesis.getHash(), 0);
        send.addOutput(50, goofy.getPublic());
        send.addOutput(50, scroodge.getPublic());

        // Signs the input with his private key
        sig.initSign(scroodge.getPrivate());
        sig.update(send.getRawDataToSign(0));
        send.addSignature(sig.sign(), 0);

        // Hashes
        send.setHash(md.digest(send.getRawTx()));

        TxHandler handler = new TxHandler(pool);
        assert(handler.isValidTx(send));
    }
}