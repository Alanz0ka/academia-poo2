package academia.dao;

import academia.excecao.AcademiaException;

import java.util.List;

/** Interface generica com as quatro operacoes do banco: incluir, alterar, excluir e consultar. */
public interface Crud<T> {

    void inserir(T objeto) throws AcademiaException;

    void atualizar(T objeto) throws AcademiaException;

    void excluir(int id) throws AcademiaException;

    T buscarPorId(int id) throws AcademiaException;

    List<T> listar() throws AcademiaException;
}
