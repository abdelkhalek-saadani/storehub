export type User = {
  name: string;
  email: string;
  password: string
}

export type SignUpParams = {
  name: string;
  email: string;
  password: string
}
export type SignInParams = Omit<SignUpParams, 'name'>;
