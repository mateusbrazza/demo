


    @InjectMocks
    private ConsentsOpinV2ServiceImpl service;

    @Mock
    private ConsentRepository repository;

    @Mock
    private ConsentExpiredRepository repositoryExpired;

    @Test
    void delete_DeveExcluirConsentimento_QuandoExiste() {
        String id = "123";
        Mockito.when(repository.existsById(id)).thenReturn(true);

        service.delete(id);

        Mockito.verify(repository).deleteById(id);
    }

    @Test
    void delete_DeveLancarExcecao_QuandoConsentimentoNaoExiste() {
        String id = "123";
        Mockito.when(repository.existsById(id)).thenReturn(false);

        ConsentOpinNotFoundException exception = assertThrows(
            ConsentOpinNotFoundException.class,
            () -> service.delete(id)
        );

        assertEquals("Consentimento não encontrado", exception.getMessage());
        Mockito.verify(repository, Mockito.never()).deleteById(Mockito.anyString());
    }
}



Package            Version
------------------ -----------
annotated-types    0.7.0
boto3              1.38.21
botocore           1.38.21
certifi            2025.4.26
cffi               1.17.1
charset-normalizer 3.4.2
colorama           0.4.6
coverage           7.8.1
cryptography       45.0.2
freezegun          1.5.1
idna               3.10
iniconfig          2.1.0
Jinja2             3.1.6
jmespath           1.0.1
MarkupSafe         3.0.2
moto               5.1.4
packaging          25.0
pip                24.0
pluggy             1.6.0
pycparser          2.22
pydantic           2.11.4
pydantic_core      2.33.2
pytest             8.3.5
pytest-cov         6.1.1
python-dateutil    2.9.0.post0
PyYAML             6.0.2
requests           2.32.3
responses          0.25.7
s3transfer         0.12.0
six                1.17.0
typing_extensions  4.13.2
typing-inspection  0.4.1
urllib3            2.4.0
Werkzeug           3.1.3
xmltodict          0.14.2
