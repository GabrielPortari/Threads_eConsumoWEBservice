package com.example.requisicoes;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.requisicoes.api.CEPService;
import com.example.requisicoes.api.DataService;
import com.example.requisicoes.model.CEP;
import com.example.requisicoes.model.Foto;
import com.example.requisicoes.model.Postagem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private Button buttonRecuperar;
    private Retrofit retrofit;
    private TextView textRecuperado;
    private List<Foto> listaFotos = new ArrayList<>();
    private List<Postagem> listaPostagem = new ArrayList<>();
    private String[] permissoes = new String[]{
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonRecuperar = findViewById(R.id.buttonRecuperar);
        textRecuperado = findViewById(R.id.textRecuperado);
        Permissao.validarPermissoes(permissoes, this, 1);

        String urlCEP = "https://viacep.com.br/ws/";
        String urlJsonPlaceholder = "https://jsonplaceholder.typicode.com";
        retrofit = new Retrofit.Builder()
                //.baseUrl(urlCEP)
                .baseUrl(urlJsonPlaceholder)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        buttonRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //recuperarCEPRetrofit();
                //recuperarListaRetrofit();
                //salvarPostagem();
                //atualizarPostagem();
                excluirPostagem();
                /*MyTask task = new MyTask();
                task.execute(urlApi);*/


            }
        });
    }

    private void excluirPostagem(){
        DataService dataService = retrofit.create(DataService.class);
        Call<Void> callPostagem =  dataService.excluirPostagem(2);
        callPostagem.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    textRecuperado.setText("Status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
    private void atualizarPostagem(){
        Postagem postagem = new Postagem("1234", null, "corpo postagem teste");

        DataService dataService = retrofit.create(DataService.class);
        Call<Postagem> callPostagem =  dataService.atualizarPostagem(2, postagem);

        callPostagem.enqueue(new Callback<Postagem>() {
            @Override
            public void onResponse(Call<Postagem> call, Response<Postagem> response) {
                if(response.isSuccessful()){
                    Postagem postagemResposta = response.body();
                    textRecuperado.setText(
                            "Codigo: " + response.code() +
                                    "\nid: " + postagemResposta.getId() +
                                    "\nuserId: " + postagemResposta.getUserId() +
                                    "\ntitulo: " + postagemResposta.getTitle() +
                                    "\nbody: " + postagemResposta.getBody()
                    );
                }
            }

            @Override
            public void onFailure(Call<Postagem> call, Throwable t) {

            }
        });
    }
    private void salvarPostagem(){
        //criar uma postagem
        //Postagem postagem = new Postagem("1234", "Titulo postagem teste", "corpo postagem teste");

        DataService dataService = retrofit.create(DataService.class);
        //Não é mais necessário criar o objeto caso utilize os campos @field
        Call<Postagem> callPostagem = dataService.salvarPostagem("1234", "Titulo postagem teste", "corpo postagem teste");

        callPostagem.enqueue(new Callback<Postagem>() {
            @Override
            public void onResponse(Call<Postagem> call, Response<Postagem> response) {
                if(response.isSuccessful()){
                    Postagem postagemResposta = response.body();
                    textRecuperado.setText(
                            "Codigo: " + response.code() +
                                    "\nid: " + postagemResposta.getId() +
                                    "\ntitulo: " + postagemResposta.getTitle()
                    );
                }
            }

            @Override
            public void onFailure(Call<Postagem> call, Throwable t) {

            }
        });

    }
    private void recuperarListaRetrofit(){
        DataService dataService = retrofit.create(DataService.class);
        Call<List<Foto>> callFoto = dataService.recuperarFotos();
        callFoto.enqueue(new Callback<List<Foto>>() {
            @Override
            public void onResponse(Call<List<Foto>> call, Response<List<Foto>> response) {
                if(response.isSuccessful()){
                    listaFotos = response.body();

                    for(int i = 0; i<listaFotos.size(); i++){
                        Foto foto = listaFotos.get(i);
                        Log.d("resultado", "Resultado foto" + foto.getId() + ", " + foto.getTitle());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Foto>> call, Throwable t) {

            }
        });
        Call<List<Postagem>> callPostagem = dataService.recuperarPostagens();
        callPostagem.enqueue(new Callback<List<Postagem>>() {
            @Override
            public void onResponse(Call<List<Postagem>> call, Response<List<Postagem>> response) {
                if(response.isSuccessful()){
                    listaPostagem = response.body();

                    for(int i = 0; i<listaPostagem.size(); i++){
                        Postagem postagem = listaPostagem.get(i);
                        Log.d("resultado", "Resultado postagem " + postagem.getId() + ", " + postagem.getTitle());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Postagem>> call, Throwable t) {

            }
        });
    }
    private void recuperarCEPRetrofit(){
        CEPService cepService = retrofit.create(CEPService.class);
        Call<CEP> call = cepService.recuperarCEP("17900000");
        call.enqueue(new Callback<CEP>() {
            @Override
            public void onResponse(Call<CEP> call, Response<CEP> response) {
                if(response.isSuccessful()){
                    CEP cep = response.body();
                    textRecuperado.setText(cep.toString());
                }
            }

            @Override
            public void onFailure(Call<CEP> call, Throwable t) {

            }
        });
    }
    class MyTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = strings[0];
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            StringBuffer stringBuffer = null;
            try {
                URL url = new URL(stringUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                //recupera dados em bytes
                inputStream = connection.getInputStream();
                //recupera os bytes e decodifica para caracteres
                inputStreamReader = new InputStreamReader(inputStream);
                //leitura dos caracteres decodificados
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                stringBuffer = new StringBuffer();
                String linha = "";

                while((linha = bufferedReader.readLine()) != null){
                    stringBuffer.append(linha);
                }

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return stringBuffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String cep = null;
            String logradouro = null;
            String complemento = null;
            String bairro = null;
            String localidade = null;
            String uf = null;
            String ibge = null;
            String gia = null;
            String ddd = null;
            String siafi = null;
            String resultado = null;

            try {
                JSONObject jsonObject = new JSONObject(s);
                cep = jsonObject.getString("cep");
                logradouro = jsonObject.getString("logradouro");
                complemento = jsonObject.getString("complemento");
                bairro = jsonObject.getString("bairro");
                localidade = jsonObject.getString("localidade");
                uf = jsonObject.getString("uf");
                ibge = jsonObject.getString("ibge");
                gia = jsonObject.getString("gia");
                ddd = jsonObject.getString("ddd");
                siafi = jsonObject.getString("siafi");

                resultado = cep + "\n" + logradouro + "\n" + complemento + "\n" +
                        bairro + "\n" + localidade + "\n" + uf + "\n" + ibge + "\n" +
                        gia + "\n" + ddd + "\n" + siafi;

            }catch (JSONException e){
                e.printStackTrace();
            }
            textRecuperado.setText(resultado);
        }
    }
}