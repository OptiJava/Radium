// @ts-ignore
export function isServerAddressValid(serverAddress) {
  try {
    const response = fetch(serverAddress);
    let ok = false;
    response.then(isValid => {
      ok = isValid.ok
    });
    return ok
  } catch (error) {
    return false;
  }
}