package org.example.app.repository.impl;

import org.example.app.config.HibernateUtils;
import org.example.app.domain.product.Product;
import org.example.app.repository.AppRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductRepository implements AppRepository<Product> {

    private static final Logger LOGGER =
            Logger.getLogger(ProductRepository.class.getName());

    @Override
    public void create(Product product) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            // Транзакція стартує
            transaction = session.beginTransaction();
            String hql = "INSERT INTO Product (productName, measure, quota, price) " +
                    "VALUES (:productName, :measure, :quota, :price)";
            MutationQuery query = session.createMutationQuery(hql);
            query.setParameter("productName", product.getProductName());
            query.setParameter("measure", product.getMeasure());
            query.setParameter("quota", product.getQuota());
            query.setParameter("price", product.getPrice());
            query.executeUpdate();
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public Optional<List<Product>> fetchAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction;
            transaction = session.beginTransaction();
            List<Product> list =
                    session.createQuery("FROM Product", Product.class).list();
            transaction.commit();
            return Optional.of(list);

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Product> fetchById(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Product> query = session.createQuery("FROM Product WHERE id = :id", Product.class);
            query.setParameter("id", id);
            query.setMaxResults(1);
            Product product = query.uniqueResult();
            transaction.commit();
            return Optional.of(product);

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public void update(Long id, Product product) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "UPDATE Product SET productName = :productName," +
                    " measure = :measure, quota = :quota, price = :price" +
                    " WHERE id = :id";
            MutationQuery query = session.createMutationQuery(hql);
            query.setParameter("productName", product.getProductName());
            query.setParameter("measure", product.getMeasure());
            query.setParameter("quota", product.getQuota());
            query.setParameter("price", product.getPrice());
            query.setParameter("id", id);
            query.executeUpdate();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "DELETE FROM Product WHERE id = :id";
            MutationQuery query = session.createMutationQuery(hql);
            query.setParameter("id", id);
            query.executeUpdate();
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public boolean isIdExists(Long id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Product product = new Product();
            product.setId(id);
            product = session.get(Product.class, product.getId());

            if (product != null) {
                Query<Product> query = session.createQuery("FROM Product", Product.class);
                query.setMaxResults(1);
                query.getResultList();
            }
            return product != null;
        }
    }

    public Optional<Product> getLastProduct() {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Product> query = session.createQuery("FROM Product ORDER BY id DESC", Product.class);
            query.setMaxResults(1);
            Product product = query.uniqueResult();
            transaction.commit();
            return Optional.of(product);

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
