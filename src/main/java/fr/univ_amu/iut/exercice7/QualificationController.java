package fr.univ_amu.iut.exercice7;

import com.google.inject.Inject;
import fr.nedjar.vigiechiro.audio.AudioView;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

/**
 * Contrôleur de vue du capstone.
 *
 * <p>Le contrôleur lie la vue au ViewModel : il abonne la TableView à la liste des séquences,
 * relaie la sélection au ViewModel, lie les libellés et les commandes.
 */
public class QualificationController {

  private static final DateTimeFormatter HEURE = DateTimeFormatter.ofPattern("HH:mm");

  @Inject private QualificationViewModel viewModel;

  @FXML private TableView<Sequence> tableSequences;
  @FXML private TableColumn<Sequence, String> colHorodatage;
  @FXML private TableColumn<Sequence, String> colFrequence;
  @FXML private TableColumn<Sequence, String> colDuree;
  @FXML private TableColumn<Sequence, String> colStatut;
  @FXML private Label labelSelection;
  @FXML private Button boutonEcouter;
  @FXML private TextArea zoneCommentaire;
  @FXML private ChoiceBox<String> choiceVerdict;
  @FXML private Label labelVerdictGlobal;
  @FXML private AudioView audioView;

  @FXML
  private void initialize() {
    chargerAudio("seq-1.wav");
    viewModel
        .sequenceSelectionneeProperty()
        .addListener(
            (obs, ancienne, seq) -> {
              if (seq != null) {
                chargerAudio(seq.getAudioRessource());
              }
            });

    colHorodatage.setCellValueFactory(
        c -> new SimpleStringProperty(c.getValue().horodatageProperty().get().format(HEURE)));
    colFrequence.setCellValueFactory(
        c ->
            new SimpleStringProperty(
                String.format(
                    "%.1f kHz", c.getValue().frequenceDominanteKHzProperty().get()))); // Double
    // ->
    // %.1f
    colDuree.setCellValueFactory(
        c ->
            new SimpleStringProperty(
                String.format("%d s", c.getValue().dureeSecondesProperty().get()))); // Integer ->
    // %d
    colStatut.setCellValueFactory(c -> c.getValue().statutProperty());

    tableSequences.setItems(viewModel.sequencesProperty());

    viewModel
        .sequenceSelectionneeProperty()
        .bind(tableSequences.getSelectionModel().selectedItemProperty());

    labelSelection.textProperty().bind(viewModel.descriptionSelectionProperty());
    labelVerdictGlobal.textProperty().bind(viewModel.verdictGlobalLibelleProperty());

    boutonEcouter.disableProperty().bind(viewModel.peutEcouterProperty().not());

    zoneCommentaire.textProperty().bindBidirectional(viewModel.commentaireProperty());
    choiceVerdict.setItems(
        javafx.collections.FXCollections.observableArrayList(viewModel.listeVerdicts()));
    choiceVerdict.valueProperty().bindBidirectional(viewModel.verdictSaisiProperty());
  }

  @FXML
  private void surEcouter() {
    viewModel.ecouterCommand();
    audioView.setPlaying(true);
  }

  @FXML
  private void surEnregistrerVerdict() {
    viewModel.enregistrerVerdictCommand();
  }

  private void chargerAudio(String ressource) {
    try {
      audioView.setAudioFile(Path.of(getClass().getResource("/audio/" + ressource).toURI()));
    } catch (Exception e) {
    }
  }
}
