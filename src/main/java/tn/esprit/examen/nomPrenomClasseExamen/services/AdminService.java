package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Admin;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.AdminRepository;

@Slf4j
@AllArgsConstructor
@Service
public class AdminService implements IAdminService {
private AdminRepository adminRepository;

  @Autowired
  private PasswordEncoder passwordEncoder; // Injection du BCryptPasswordEncoder

  @Override
  public Admin createAdmin(Admin admin) {
    // Hachage du mot de passe avant de le sauvegarder
    String encryptedPassword = passwordEncoder.encode(admin.getUserPassword());
    admin.setUserPassword(encryptedPassword);
    return adminRepository.save(admin);  }

}
