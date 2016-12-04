import java.util.*;
import java.util.stream.Collectors;

public class TxHandler {

  private UTXOPool pool;

  /**
   * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
   * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
   * constructor.
   */
  public TxHandler(UTXOPool utxoPool) {
    this.pool = new UTXOPool(utxoPool);
  }

  /**
   * @return true if: (1) all outputs claimed by {@code tx} are in the current UTXO pool, (2) the
   *     signatures on each input of {@code tx} are valid, (3) no UTXO is claimed multiple times by
   *     {@code tx}, (4) all of {@code tx}s output values are non-negative, and (5) the sum of
   *     {@code tx}s input values is greater than or equal to the sum of its output values; and
   *     false otherwise.
   */
  public boolean isValidTx(Transaction tx) {
    // (1)
    boolean areAllOutputsInPool =
        tx.getInputs().stream().allMatch(input -> pool.contains(new UTXO(input.prevTxHash, input.outputIndex)));

    System.out.println("areAllOutputsInPool: " + areAllOutputsInPool);

    // (2)
    boolean areSignaturesValid = true;
    for (int i = 0; i < tx.getInputs().size(); i++) {
      if (!isSignatureValid(tx, tx.getInput(i), i)) {
        areSignaturesValid = false;
      }
    }
    System.out.println("areSignaturesValid: " + areSignaturesValid);

    // (3)
    List<UTXO> claimedUTXOs =
        tx.getInputs()
            .stream()
            .map(input -> new UTXO(input.prevTxHash, input.outputIndex))
            .collect(Collectors.toList());
    Set<UTXO> uniqueClaimedUTXOs = new HashSet<>(claimedUTXOs);
    boolean areUTXOsClaimedAtMostOnce = claimedUTXOs.size() == uniqueClaimedUTXOs.size();
    System.out.println("areUTXOsClaimedAtMostOnce " + areUTXOsClaimedAtMostOnce);

    // (4)
    boolean areAllOutputValuesNonNegative =
        tx.getOutputs().stream().allMatch(output -> output.value >= 0);
    System.out.println("areAllOutputValuesNonNegative: " + areAllOutputValuesNonNegative);

    // (5)
    double outputsTotal = tx.getOutputs().stream().mapToDouble(output -> output.value).sum();
    double inputsTotal =
        tx.getInputs()
            .stream()
            .map(this::getCorrespondingOutput)
            .filter(Optional::isPresent)
            .mapToDouble(outputOpt -> outputOpt.get().value)
            .sum();
    boolean areInputValuesGreaterOrEqualToOutputValues = inputsTotal >= outputsTotal;

    System.out.println("areInputValuesGreaterOrEqualToOutputValues: " + areInputValuesGreaterOrEqualToOutputValues);

    return areAllOutputsInPool
        && areSignaturesValid
        && areUTXOsClaimedAtMostOnce
        && areAllOutputValuesNonNegative
        && areInputValuesGreaterOrEqualToOutputValues;
  }

  private boolean isSignatureValid(Transaction tx, Transaction.Input input, int inputIndex) {
    Optional<Transaction.Output> correspondingOutput = getCorrespondingOutput(input);
    if (correspondingOutput.isPresent()) {
      Transaction.Output output = correspondingOutput.get();
      return Crypto.verifySignature(
          output.address, tx.getRawDataToSign(inputIndex), input.signature);
    } else {
      return false;
    }
  }

  private Optional<Transaction.Output> getCorrespondingOutput(Transaction.Input input) {
    return pool.getAllUTXO()
        .stream()
        .filter(utxo -> utxo.equals(new UTXO(input.prevTxHash, input.outputIndex)))
        .map(utxo -> pool.getTxOutput(utxo))
        .findFirst();
  }

  /**
   * Handles each epoch by receiving an unordered array of proposed transactions, checking each
   * transaction for correctness, returning a mutually valid array of accepted transactions, and
   * updating the current UTXO pool as appropriate.
   */
  public Transaction[] handleTxs(Transaction[] possibleTxs) {
    List<Transaction> txsHandled = new ArrayList<>();
    for (int i = 0; i < possibleTxs.length; i++) {
      handleTx(possibleTxs[i]).ifPresent(tx -> txsHandled.add(tx));
    }
    return txsHandled.toArray(new Transaction[txsHandled.size()]);
  }

  /**
   * Returns the Optional of the Transaction.
   *
   * <p>The result is empty if the tx is not handled. It will contain the Transaction if it is
   * handled.
   */
  private Optional<Transaction> handleTx(Transaction tx) {
    if (isValidTx(tx)) {
      for (int inputIndex = 0; inputIndex < tx.getInputs().size(); inputIndex++) {
        Transaction.Input input = tx.getInput(inputIndex);
        UTXO utxoToRemove = new UTXO(input.prevTxHash, input.outputIndex);
        pool.removeUTXO(utxoToRemove);
      }
      for (int outputIndex = 0; outputIndex < tx.getOutputs().size(); outputIndex++) {
        UTXO utxoToAdd = new UTXO(tx.getHash(), outputIndex);
        pool.addUTXO(utxoToAdd, tx.getOutput(outputIndex));
      }
      return Optional.of(tx);
    } else {
      return Optional.empty();
    }
  }
}
